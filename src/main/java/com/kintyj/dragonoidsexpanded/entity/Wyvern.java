package com.kintyj.dragonoidsexpanded.entity;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.kintyj.dragonoidsexpanded.DragonoidsExpanded;
import com.kintyj.dragonoidsexpanded.brain.behaviour.LeapAtTarget;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.GlowSquid;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.common.util.FriendlyByteBufUtil;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.FollowOwner;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.InvalidateAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.TargetOrRetaliate;
import net.tslat.smartbrainlib.api.core.navigation.SmoothFlyingPathNavigation;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.custom.NearbyItemsSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyAdultSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import net.tslat.smartbrainlib.util.BrainUtils;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class Wyvern extends TamableAnimal
        implements Enemy, GeoEntity, SmartBrainOwner<Wyvern>, InventoryCarrier {

    private static final int yawnDelayMin = 177;
    private static final int yawnDelayMax = 1036;
    // private static final int blinkDelay = 300;
    // private static final int blinkTime = 25;

    // public int blinkTimer;
    // public boolean blinking = true;

    // public boolean isBlinking() {
    // return blinking;
    // }

    private static final EntityDataAccessor<Integer> TYPE = SynchedEntityData
            .defineId(Wyvern.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData
            .defineId(Wyvern.class, EntityDataSerializers.INT);

    // #region Base Stats
    private final SimpleContainer inventory = new SimpleContainer(1);

    public static AttributeSupplier.Builder createMobAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 50.0)
                .add(Attributes.ATTACK_DAMAGE, 5.0)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0)
                .add(Attributes.ATTACK_SPEED, 2.4)
                .add(Attributes.FOLLOW_RANGE, 50.0)
                .add(Attributes.MOVEMENT_SPEED, 0.4);
    }
    // #endregion

    @VisibleForDebug
    @Override
    public SimpleContainer getInventory() {
        return this.inventory;
    }

    @Override
    public boolean isFood(@Nonnull ItemStack stack) {
        return stack.is(Items.BEEF);
    }

    @Override
    @Nullable
    public AgeableMob getBreedOffspring(@Nonnull ServerLevel level, @Nonnull AgeableMob otherParent) {
        return null;
    }

    // #region Colors
    @Override
    protected void defineSynchedData(@Nonnull SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TYPE, 0);
        builder.define(COLOR, 0);
    }

    public int getWyvernType() {
        return this.entityData.get(TYPE);
    }

    public void setWyvernType(int type) {
        this.entityData.set(TYPE, type);
        DragonoidsExpanded.LOGGER.info("Type: " + type);
    }

    public int getColor() {
        return this.entityData.get(COLOR);
    }

    public void setColor(int color) {
        this.entityData.set(COLOR, color);
    }
    // #endregion\

    // #region Animations
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void registerControllers(ControllerRegistrar controllers) {

        controllers.add(new AnimationController<>(this, "wingController", 20, event -> {
            if (event.isMoving()) {
                return event.setAndContinue(
                        (RawAnimation.begin().thenLoop("animation.wyvern.wings_folded")));
            } else {
                return event.setAndContinue(RawAnimation.begin().thenPlay("animation.wyvern.wings_folded"));
            }
        }));

        controllers.add(new AnimationController<>(this, "attackController", 10, event -> {
            return PlayState.CONTINUE;
        }).triggerableAnim("bite", RawAnimation.begin().thenPlay("animation.wyvern.bite"))
                .triggerableAnim("yawn", RawAnimation.begin().thenPlay("animation.wyvern.yawn")));

        controllers.add(new AnimationController<>(this, "bodyController", 10, event -> {
            if (event.isMoving()) {
                if (this.isAggressive()) {
                    return event.setAndContinue((RawAnimation.begin().thenLoop("animation.wyvern.walk_1")));
                } else {
                    return event.setAndContinue((RawAnimation.begin().thenLoop("animation.wyvern.walk_1")));
                }
            } else {
                return event.setAndContinue(RawAnimation.begin().thenPlay("animation.wyvern.idle_1"));
            }
        }));

        // controllers.add(new AnimationController<>(this,"turnController", event -> {
        // if (event.isTurning()) {

        // return
        // event.setAndContinue(RawAnimation.begin().thenPlay("animation.wyvern.turn_right"));
        // } else {
        // return
        // event.setAndContinue(RawAnimation.begin().thenPlay("animation.wyvern.turn_left"));
        // }
        // }));
    }
    // #endregion

    // #region Immunities
    @Override
    public boolean hurt(@Nonnull DamageSource source, float amount) {
        if (source.is(DamageTypes.FALL))
            return false;
        return super.hurt(source, amount);
    }
    // #endregion

    // #region Ai Step

    private int timer = 0;

    @Override
    public void aiStep() {

        if (!level().isClientSide) {
            if (timer <= 0) {
                timer = getRandom().nextIntBetweenInclusive(yawnDelayMin, yawnDelayMax);
                triggerAnim("attackController", "yawn");
                playSound(DragonoidsExpanded.WYVERN_CALL.get());

            } else {
                timer--;
            }
        }
        super.aiStep();
    }
    // #endregion

    // #region Sensors
    @Override
    public List<ExtendedSensor<? extends Wyvern>> getSensors() {
        return ObjectArrayList.of(
                new NearbyLivingEntitySensor<Wyvern>(),
                new NearbyAdultSensor<>(),
                new HurtBySensor<>(),
                new NearbyItemsSensor<>());
    }
    // #endregion

    @Override
    protected void customServerAiStep() {
        tickBrain(this);
    }

    // #region Brains
    @Override
    protected Brain.Provider<?> brainProvider() {
        return new SmartBrainProvider<>(this);
    }

    @Override
    protected PathNavigation createNavigation(@Nonnull Level pLevel) {
        SmoothFlyingPathNavigation navigation = new SmoothFlyingPathNavigation(this, pLevel);

        return navigation;
    }

    @Override
    public BrainActivityGroup<? extends Wyvern> getCoreTasks() { // These are the tasks that run all the time
                                                                 // (usually)
        return BrainActivityGroup.coreTasks(

                new LookAtTarget<Wyvern>(),

                new MoveToWalkTarget<>()); // Walk towards
                                           // the current
                                           // walk target
    }

    @SuppressWarnings({ "unchecked", "null" })
    @Override
    public BrainActivityGroup<? extends Wyvern> getIdleTasks() { // These are the tasks that run when the mob
                                                                 // isn't doing anything else (usually)
        return BrainActivityGroup.idleTasks(
                new FirstApplicableBehaviour<>(
                        new TargetOrRetaliate<>().attackablePredicate(
                                (target) -> !(target instanceof Wyvern
                                        || target instanceof Creeper
                                        || target instanceof Bat
                                        || target instanceof GlowSquid
                                        || (this.getOwner() != null && this.getOwner().is(target))
                                        || (this.getOwner() != null && !(target instanceof Mob))))),
                new FirstApplicableBehaviour<>(
                        new TargetOrRetaliate<>(),
                        new SetPlayerLookTarget<>(),
                        new FollowOwner<>().teleportToTargetAfter(128).stopFollowingWithin(24)),
                new OneRandomBehaviour<>(
                        new SetRandomWalkTarget<Wyvern>().speedModifier(0.6f),
                        new Idle<>().runFor(entity -> entity.getRandom().nextInt(30, 60))));
    }

    @SuppressWarnings({ "unchecked", "null" })
    @Override
    public BrainActivityGroup<? extends Wyvern> getFightTasks() {
        return BrainActivityGroup.fightTasks(new InvalidateAttackTarget<>(),
                new SetWalkTargetToAttackTarget<>(),
                new FirstApplicableBehaviour<>(
                        new LeapAtTarget<>(0)
                                .verticalJumpStrength(((mob, entity) -> {
                                    float distanceToEntity = (float) Math.abs(entity.position().y - mob.position().y);
                                    return 0.5f + distanceToEntity / 6f;
                                }))
                                .whenStarting(entity -> {
                                    setAggressive(true);
                                })
                                .whenStopping(entity -> setAggressive(false)).startCondition(entity -> {
                                    return (BrainUtils.getTargetOfEntity(entity) != null
                                            && BrainUtils.getTargetOfEntity(entity).distanceTo(entity) > 7);
                                }).cooldownFor((entity) -> 120), // Set the walk target to the attack target
                        new AnimatableMeleeAttack<>(12).whenStarting(entity -> {
                            setAggressive(true);
                            triggerAnim("attackController", "bite");
                        }).whenStopping(entity -> setAggressive(false))

                ));
    }
    // #endregion

    @Override
    public boolean wantsToPickUp(@Nonnull ItemStack stack) {
        return stack.is(Items.IRON_INGOT);
    }

    public Wyvern(EntityType<? extends Wyvern> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public class WyvernColor {
        public WyvernColor() {

        }
    }

}

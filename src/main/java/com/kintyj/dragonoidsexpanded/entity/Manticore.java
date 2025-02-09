package com.kintyj.dragonoidsexpanded.entity;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.kintyj.dragonoidsexpanded.DragonoidsExpanded;
import com.kintyj.dragonoidsexpanded.brain.behaviour.LeapAtTarget;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
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

public class Manticore extends TamableAnimal
        implements Enemy, GeoEntity, SmartBrainOwner<Manticore>, InventoryCarrier {
    private static final int roarDelayMin = 500;
    private static final int roarDelayMax = 1500;
    // private static final int blinkDelay = 300;
    // private static final int blinkTime = 25;

    // public int blinkTimer;
    // public boolean blinking = true;

    // public boolean isBlinking() {
    // return blinking;

    // #region Base Stats
    private final SimpleContainer inventory = new SimpleContainer(1);

    public static AttributeSupplier.Builder createMobAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 60.0)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
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
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        // TODO Auto-generated method stub
        return null;
    }

    // #region Animations
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void registerControllers(ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "attackController", event -> {
            return PlayState.CONTINUE;
        }).triggerableAnim("bite", RawAnimation.begin().thenPlay("animation.manticore.bite"))
                .triggerableAnim("leftStrike", RawAnimation.begin().thenPlay("animation.manticore.left_strike"))
                .triggerableAnim("rightStrike", RawAnimation.begin().thenPlay("animation.manticore.right_strike"))
                .triggerableAnim("roar", RawAnimation.begin().thenPlay("animation.manticore.roar")));

        controllers.add(new AnimationController<>(this, "tailController", 20, event -> {
            if (event.isMoving()) {
                return event.setAndContinue(
                        (RawAnimation.begin().thenLoop("animation.manticore.walk_stinger")));
            } else { // Turning left
                return event.setAndContinue(RawAnimation.begin().thenPlay("animation.manticore.idle_stinger"));
            }

        }));
        controllers.add(new AnimationController<>(this, "bodyController", 10, event -> {
            if (event.isMoving()) {
                if (this.isAggressive()) {
                    return event.setAndContinue((RawAnimation.begin().thenLoop("animation.manticore.sprint")));
                } else {
                    return event.setAndContinue((RawAnimation.begin().thenLoop("animation.manticore.walk")));
                }
            } else {
                return event.setAndContinue(RawAnimation.begin().thenPlay("animation.manticore.idle"));
            }
        }));
    }
    // #endregion

    // #region Ai Step
    private int timer = 0;

    @Override
    public void aiStep() {

        if (timer <= 0) {
            timer = getRandom().nextIntBetweenInclusive(roarDelayMin, roarDelayMax);
            triggerAnim("attackController", "roar");
            playSound(DragonoidsExpanded.MANTICORE_ROAR.get());

        } else {
            timer--;
        }
        super.aiStep();
    }
    // #endregion

    // #region Sensors
    @Override
    public List<ExtendedSensor<? extends Manticore>> getSensors() {
        return ObjectArrayList.of(
                new NearbyLivingEntitySensor<Manticore>(),
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
    public BrainActivityGroup<? extends Manticore> getCoreTasks() { // These are the tasks that run all the time
                                                                    // (usually)
        return BrainActivityGroup.coreTasks(

                new LookAtTarget<Manticore>(),

                new MoveToWalkTarget<>()); // Walk towards
                                           // the current
                                           // walk target
    }

    @SuppressWarnings({ "unchecked", "null" })
    @Override
    public BrainActivityGroup<? extends Manticore> getIdleTasks() { // These are the tasks that run when the mob
                                                                    // isn't doing anything else (usually)
        return BrainActivityGroup.idleTasks(
                new FirstApplicableBehaviour<>(
                        new TargetOrRetaliate<>(),
                        new SetPlayerLookTarget<>(),
                        new FollowOwner<>().teleportToTargetAfter(32).stopFollowingWithin(12)),
                new OneRandomBehaviour<>(
                        new SetRandomWalkTarget<Manticore>().speedModifier(0.5f),
                        new Idle<>().runFor(entity -> entity.getRandom().nextInt(30, 60))));
    }

    @SuppressWarnings({ "unchecked", "null" })
    @Override
    public BrainActivityGroup<? extends Manticore> getFightTasks() { // These are the tasks that handle fighting
        return BrainActivityGroup.fightTasks(new InvalidateAttackTarget<>(), // Cancel fighting if the
                                                                             // target is
                // no
                // longer valid
                new SetWalkTargetToAttackTarget<>(),
                new FirstApplicableBehaviour<>(
                        new LeapAtTarget<>(0)
                                .verticalJumpStrength(((mob, entity) -> {
                                    float distanceToEntity = (float) Math.abs(entity.position().y - mob.position().y);
                                    return 0.5f + distanceToEntity / 6f;
                                }))
                                .leapRange((mob, entity) -> 25f)
                                .jumpStrength(((mob, entity) -> {
                                    return (float) entity.getAttribute(Attributes.JUMP_STRENGTH).getBaseValue();
                                }))
                                .whenStarting(entity -> {
                                    setAggressive(true);
                                    // triggerAnim("defaultController", "jump"); (No SFX)
                                })
                                .whenStopping(entity -> setAggressive(false)).startCondition(entity -> {
                                    return (BrainUtils.getTargetOfEntity(entity) != null
                                            && BrainUtils.getTargetOfEntity(entity).distanceTo(entity) > 7);
                                }).cooldownFor((entity) -> 120), // Set the walk target to the attack target
                        new AnimatableMeleeAttack<>(7).whenStarting(entity -> {
                            setAggressive(true);
                            int attackOption = this.getRandom().nextInt(0, 3);
                            switch (attackOption) {
                                case 0:
                                    triggerAnim("attackController", "bite");
                                    DragonoidsExpanded.LOGGER.debug("bite");
                                    break;
                                case 1:
                                    triggerAnim("attackController", "leftStrike");
                                    DragonoidsExpanded.LOGGER.debug("leftStrike");
                                    break;
                                case 2:
                                    triggerAnim("attackController", "rightStrike");
                                    DragonoidsExpanded.LOGGER.debug("rightStrike");
                                    break;
                            }

                        }).whenStopping(entity -> setAggressive(false))
                                .cooldownFor(entity -> 10)));
    }
    // #endregion

    @Override
    public boolean wantsToPickUp(ItemStack stack) {
        return stack.is(Items.IRON_INGOT);
    }

    public Manticore(EntityType<? extends Manticore> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
}

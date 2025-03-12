package com.kintyj.dragonoidsexpanded.entity;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.kintyj.dragonoidsexpanded.DragonoidsExpanded;
import com.kintyj.dragonoidsexpanded.brain.behaviour.DesireItem;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
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
import net.minecraft.world.entity.player.Player;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.ConditionlessAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.LeapAtTarget;
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
import net.tslat.smartbrainlib.util.BrainUtil;
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

    private static final float STING_DAMAGE = 10.0f;
    private static final int TICK_TIME = 20;
    private static final int CHEWING_TIME = 120;

    int timerTwo = 0;
    boolean hasTarget = false;

    int chewingTimer = 0;
    boolean isChewing = false;

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
    public AgeableMob getBreedOffspring(@Nonnull ServerLevel level, @Nonnull AgeableMob otherParent) {
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
        }).triggerableAnim("chew", RawAnimation.begin().thenPlayXTimes("animation.manticore.chew", 4)));
    }
    // #endregion

    // #region Ai Step
    private int timer = 0;

    @Override
    public void aiStep() {
        if (isChewing) {
            if (chewingTimer == CHEWING_TIME) {
                if (!level().isClientSide) {
                    triggerAnim("bodyController", "chew");
                    playSound(DragonoidsExpanded.MANTICORE_CHEW.get());
                }
                chewingTimer--;
            } else if (chewingTimer <= 0) {
                isChewing = false;
            } else {
                chewingTimer--;
            }
        }

        if (!level().isClientSide) {
            if (timer <= 0) {
                timer = getRandom().nextIntBetweenInclusive(roarDelayMin, roarDelayMax);
                triggerAnim("attackController", "roar");
                playSound(DragonoidsExpanded.MANTICORE_ROAR.get());

            } else {
                timer--;
            }
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
            new NearbyItemsSensor<>()
        );
    }
    // #endregion

    @Override
    protected void customServerAiStep(@Nonnull ServerLevel level) {
        if (!isChewing) {
            tickBrain(this);
        }
    }

    // #region Brains
    @Override
    protected Brain.Provider<?> brainProvider() {
        return new SmartBrainProvider<>(this);
    }

    @Override
    public BrainActivityGroup<? extends Manticore> getCoreTasks() { // These are the tasks that run all the time
        return BrainActivityGroup.coreTasks(
            new LookAtTarget<Manticore>(),
            new MoveToWalkTarget<>()
        );
    }

    @SuppressWarnings({ "unchecked", "null" })
    @Override
    public BrainActivityGroup<? extends Manticore> getIdleTasks() { // These are the tasks that run when the mob
                                                                    // isn't doing anything else (usually)
        return BrainActivityGroup.idleTasks(
            new FirstApplicableBehaviour<>(
                new DesireItem<Manticore>()
                    .success((entity) -> {
                        entity.timerTwo = TICK_TIME;
                        entity.hasTarget = true;
                        return true;
                    })
                    .fail((entity) -> {
                        entity.timerTwo = 0;
                        entity.hasTarget = false;
                        return false;
                    })
                    .hasItem((entity, item) -> {
                        ItemStack stack = item.getItem();
                        stack.shrink(1);
                        item.setItem(stack);
                        entity.timerTwo = 0;
                        entity.hasTarget = false;
                        entity.isChewing = true;
                        entity.chewingTimer = CHEWING_TIME;
                    }),
                new TargetOrRetaliate<>()
                    .attackablePredicate(
                        (target) -> !(target instanceof Manticore
                            || (this.getOwner() != null && this.getOwner().is(target))
                            || (this.getOwner() != null && !(target instanceof Mob))
                            || (target instanceof Player && ((Player) target).isCreative()))),
                new SetPlayerLookTarget<>(),
                new FollowOwner<>()
                    .teleportToTargetAfter(32)
                    .stopFollowingWithin(12)
            ),
            new OneRandomBehaviour<>(
                new SetRandomWalkTarget<Manticore>()
                    .speedModifier(0.5f),
                new Idle<>()
                    .runFor(entity -> entity.getRandom().nextInt(30, 60))
            )
        );
    }

    @SuppressWarnings({ "unchecked", "null" })
    @Override
    public BrainActivityGroup<? extends Manticore> getFightTasks() { // These are the tasks that handle fighting
        return BrainActivityGroup.fightTasks(
            new InvalidateAttackTarget<Manticore>()
                .invalidateIf((manticore, target) -> {
                    if (manticore.timerTwo <= 0) {
                        manticore.hasTarget = false;
                    } else {
                        manticore.timerTwo--;
                    }
                    return manticore.hasTarget;
                }),
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
                        return (BrainUtil.getTargetOfEntity(entity) != null
                                && BrainUtil.getTargetOfEntity(entity).distanceTo(entity) > 7);
                    })
                    .cooldownFor((entity) -> 120), // Set the walk target to the attack target
                new AnimatableMeleeAttack<>(7)
                    .whenStarting(entity -> {
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
                    })
                    .whenStopping(entity -> setAggressive(false))
                    .cooldownFor(entity -> 10),
                new ConditionlessAttack<>(7)
                    .whenStarting(entity -> {
                        setAggressive(true);
                        triggerAnim("attackController", "leftStrike");
                        LivingEntity target = BrainUtil.getTargetOfEntity(entity);
                        if (entity.level() instanceof ServerLevel serverLevel) {
                            target.hurtServer(serverLevel, damageSources().mobAttack(entity), STING_DAMAGE);
                            target.addEffect(new MobEffectInstance(DragonoidsExpanded.MORTIS, 60, target.hasEffect(DragonoidsExpanded.MORTIS) ? Math.clamp(target.getEffect(DragonoidsExpanded.MORTIS).getAmplifier() + 1, 1, 5) : 1));
                        }
                    })
                    .whenStopping(entity -> setAggressive(false))
                    .cooldownFor(entity -> 80)
                    .startCondition((entity) -> {
                        LivingEntity target = BrainUtil.getTargetOfEntity(entity);
                        return target != null && entity.distanceTo(target) < 4;
                    })
                )
            );
    }
    // #endregion

    @Override
    public boolean wantsToPickUp(@Nonnull ServerLevel level, @Nonnull ItemStack stack) {
        return stack.is(Items.IRON_INGOT);
    }

    public Manticore(EntityType<? extends Manticore> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
}

package com.kintyj.dragonoidsexpanded.entity;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.kintyj.dragonoidsexpanded.DragonoidsExpanded;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.GlowSquid;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.LeapAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.BreedWithPartner;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.FollowOwner;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.InvalidateAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.TargetOrRetaliate;
import net.tslat.smartbrainlib.api.core.navigation.SmoothAmphibiousPathNavigation;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.InWaterSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyAdultSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import net.tslat.smartbrainlib.util.BrainUtil;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.animation.RawAnimation;

public class FrilledDrake extends TamableAnimal
        implements Enemy, GeoEntity, PlayerRideableJumping, SmartBrainOwner<FrilledDrake> {
    private static final int yawnDelayMin = 200;
    private static final int yawnDelayMax = 1200;
    private static final int blinkDelay = 300;
    private static final int blinkTime = 30;

    public int blinkTimer;
    public boolean blinking = true;

    public boolean isBlinking() {
        return blinking;
    }

    public FrilledDrake(EntityType<? extends FrilledDrake> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    private static final EntityDataAccessor<Integer> GROWTH_SCORE = SynchedEntityData.defineId(FrilledDrake.class,
            EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> ATTACK_RANGE = SynchedEntityData.defineId(FrilledDrake.class,
            EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> STATE = SynchedEntityData.defineId(FrilledDrake.class,
            EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData
            .defineId(FrilledDrake.class, EntityDataSerializers.INT);

    public enum DrakeState {
        SLEEPING(0),
        AWAKE(1),
        SITTING(2);

        private final int state;

        DrakeState(int state) {
            this.state = state;
        }

        public int getState() {
            return state;
        }
    }

    public enum DrakeColor {
        BLUE(0),
        AQUA(1),
        TURQUOISE(2),
        GREEN(3);

        private final int color;

        DrakeColor(int color) {
            this.color = color;
        }

        public int getColor() {
            return color;
        }
    }

    public static enum DrakeAge {
        EGG(0),
        HATCHLING(4),
        DRAKELING(24),
        TEEN(104),
        ADULT(204),
        ELDER(304),
        MAX_GROWTH(504);

        public static final int TIME_BETWEEN_GROWTH = 6000;

        private final int age;

        DrakeAge(int age) {
            this.age = age;
        }

        public int getAge() {
            return age;
        }
    }

    // #region Base Stats
    protected boolean isJumping;
    protected float playerJumpPendingScale;

    private static final float BASE_ATTACK_DAMAGE = 10;
    private static final float BASE_ATTACK_SPEED = 2.4f;
    private static final float BASE_ATTACK_KNOCKBACK = 2.4f;
    private static final float BASE_STEP_HEIGHT = 3;
    private static final float BASE_MOVEMENT_SPEED = 0.3f;
    private static final float BASE_SCALE = 1f;
    private static final float BASE_HEALTH = 225f;
    private static final float ARMOR = 6;
    private static final float KNOCKBACK_RESIST = 25;
    private static final float BASE_JUMP_STRENGTH = 1.5f;
    private static final float BASE_ATTACK_RANGE = 2.5f;
    // #endregion

    public int getGrowthScore() {
        return this.entityData.get(GROWTH_SCORE);
    }

    public void setGrowthScore(int pGrowthScore) {
        this.entityData.set(GROWTH_SCORE, pGrowthScore);
        if (pGrowthScore == DrakeAge.HATCHLING.getAge())
            triggerAnim("defaultController", "hatch");
        updateScale(pGrowthScore);
    }

    public float getAttackRange() {
        return this.entityData.get(ATTACK_RANGE);
    }

    public void setAttackRange(float pAttackRange) {
        this.entityData.set(ATTACK_RANGE, pAttackRange);
    }

    public int getState() {
        return this.entityData.get(STATE);
    }

    public void setState(int pState) {
        this.entityData.set(STATE, pState);
    }

    @Override
    protected void defineSynchedData(@Nonnull SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(GROWTH_SCORE, 0);
        builder.define(ATTACK_RANGE, BASE_ATTACK_RANGE);
        builder.define(STATE, DrakeState.AWAKE.getState());
        builder.define(COLOR, DrakeColor.BLUE.getColor());
    }

    private boolean isIncubating() {
        return (getGrowthScore() < DrakeAge.HATCHLING.getAge()
                && level().getBlockState(this.blockPosition().below()).is(Blocks.SLIME_BLOCK)
                && level().getBlockState(this.blockPosition().north()).is(Blocks.SLIME_BLOCK)
                && level().getBlockState(this.blockPosition().south()).is(Blocks.SLIME_BLOCK)
                && level().getBlockState(this.blockPosition().west()).is(Blocks.SLIME_BLOCK)
                && level().getBlockState(this.blockPosition().east()).is(Blocks.SLIME_BLOCK));
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide) {
            if (this.tickCount % DrakeAge.TIME_BETWEEN_GROWTH == 0 && getGrowthScore() < DrakeAge.MAX_GROWTH.getAge()
                    && (getGrowthScore() >= DrakeAge.HATCHLING.getAge() || isIncubating())) {
                setGrowthScore(getGrowthScore() + 1);
            }
        }

        this.setYRot(this.interpolateRotation(this.getYRot(), targetYaw, 5.0F));

        this.targetYaw = this.getYHeadRot();
    }

    // #region Stat Growth
    private void updateScale(int growth) {
        float scale = (growth / (float) DrakeAge.MAX_GROWTH.getAge());
        this.setScale(scale);
    }

    @SuppressWarnings("null")
    public void setScale(float scale) {
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_ATTACK_DAMAGE * (0.1f + scale * (2.5f - 0.1f)));
        this.getAttribute(Attributes.ATTACK_SPEED).setBaseValue(BASE_ATTACK_SPEED * (0.5f + scale * (1.5f - 0.5f)));
        this.getAttribute(Attributes.ATTACK_KNOCKBACK)
                .setBaseValue(BASE_ATTACK_KNOCKBACK * (0.15f + scale * (1.25f - 0.15f)));
        setAttackRange(BASE_ATTACK_RANGE * (0.5f + scale * (1.5f - 0.5f)));
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(BASE_STEP_HEIGHT * (0.5f + scale * (2f - 0.5f)));
        this.getAttribute(Attributes.MOVEMENT_SPEED)
                .setBaseValue(BASE_MOVEMENT_SPEED * (0.85f + scale * (1.5f - 0.85f)));
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(BASE_HEALTH * (0.25f + scale * (2f - 0.25f)));
        this.getAttribute(Attributes.ARMOR).setBaseValue(ARMOR * (0.1f + scale * (1f - 0.1f)));
        this.getAttribute(Attributes.KNOCKBACK_RESISTANCE)
                .setBaseValue(KNOCKBACK_RESIST * (0.1f + scale * (1f - 0.1f)));
        this.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(BASE_JUMP_STRENGTH * (0.25f + scale * (2f - 0.25f)));
        this.setHealth((float) this.getAttribute(Attributes.MAX_HEALTH).getBaseValue());

        this.getAttribute(Attributes.SCALE).setBaseValue(BASE_SCALE * (0.25f + scale * (5.0f - 0.25f)));
        this.refreshDimensions();
    }
    // #endregion

    public void setColor(int color) {
        this.entityData.set(COLOR, color);
    }

    public int getColor() {
        return this.entityData.get(COLOR);
    }

    @Override
    public AgeableMob getBreedOffspring(@Nonnull ServerLevel serverLevel, @Nonnull AgeableMob partner) {
        FrilledDrake child = (FrilledDrake) this.getType().create(serverLevel, null, blockPosition(), EntitySpawnReason.BREEDING, true, false);
        if (child != null) {
            child.setGrowthScore(0);
            child.setColor(this.getColor());
        }
        return child;
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
        }).triggerableAnim("bite", RawAnimation.begin().thenPlay("animation.frilled_drake.bite"))
                .triggerableAnim("yawn", RawAnimation.begin().thenPlay("animation.frilled_drake.yawn")));
        controllers.add(new AnimationController<>(this, "defaultController", 3, event -> {
            if (this.getGrowthScore() < DrakeAge.HATCHLING.getAge()) {
                return event.setAndContinue(
                        isIncubating() ? RawAnimation.begin().thenLoop("animation.frilled_drake.incubate")
                                : RawAnimation.begin().thenLoop("animation.frilled_drake.egg"));
            }

            float currentYaw = this.getYRot();
            float deltaYaw = Mth.wrapDegrees(targetYaw - currentYaw);

            float sidewaysAmount = 0.0f;
            boolean sideways = true;

            if (!this.onGround()) {
                return event.setAndContinue(RawAnimation.begin().thenPlay("animation.frilled_drake.in_air"));
            }

            if (!event.isMoving()) {
                if (Math.abs(deltaYaw) > 5.0F) {
                    if (deltaYaw > 0) { // Turning right
                        return event
                                .setAndContinue(RawAnimation.begin().thenPlay("animation.frilled_drake.turn_right"));
                    } else { // Turning left
                        return event.setAndContinue(RawAnimation.begin().thenPlay("animation.frilled_drake.turn_left"));
                    }
                } else { // If not turning, ensure the idle animation starts
                    if (this.isAggressive()) {
                        return event.setAndContinue(
                                (this.isInWater() ? RawAnimation.begin().thenLoop("animation.frilled_drake.angry_float")
                                        : RawAnimation.begin().thenLoop("animation.frilled_drake.idle")));
                    } else {
                        return event.setAndContinue(
                                (this.isInWater() ? RawAnimation.begin().thenLoop("animation.frilled_drake.float")
                                        : (this.getState() == DrakeState.AWAKE.getState()
                                                ? RawAnimation.begin().thenLoop("animation.frilled_drake.idle")
                                                : (this.getState() == DrakeState.SITTING.getState()
                                                        ? RawAnimation.begin()
                                                                .thenLoop("animation.frilled_drake.sit_idle")
                                                        : RawAnimation.begin()
                                                                .thenLoop("animation.frilled_drake.sleeping")))));
                    }
                }
            } else if (this.isAggressive()) {
                return event.setAndContinue((this.isInWater()
                        ? RawAnimation.begin().thenLoop("animation.frilled_drake.aggressive_swim")
                        : RawAnimation.begin().thenLoop("animation.frilled_drake.intimidate")));
            } else {
                return event.setAndContinue(
                        (this.isInWater() ? RawAnimation.begin().thenLoop("animation.frilled_drake.swim")
                                : (sideways ? RawAnimation.begin().thenLoop("animation.frilled_drake.walk")
                                        : (sidewaysAmount > 0.0f
                                                ? RawAnimation.begin().thenLoop("animation.frilled_drake.strafe_left")
                                                : RawAnimation.begin()
                                                        .thenLoop("animation.frilled_drake.strafe_right")))));
            }
        }).triggerableAnim("jump", RawAnimation.begin().thenPlay("animation.frilled_drake.jump"))
                .triggerableAnim("claw_strike_left",
                        RawAnimation.begin().thenPlay("animation.frilled_drake.claw_strike_left"))
                .triggerableAnim("claw_strike_right",
                        RawAnimation.begin().thenPlay("animation.frilled_drake.claw_strike_right"))
                .triggerableAnim("animation.frilled_drake.turn_right",
                        RawAnimation.begin().thenPlay("animation.frilled_drake.turn_right"))
                .triggerableAnim("animation.frilled_drake.turn_left",
                        RawAnimation.begin().thenPlay("animation.frilled_drake.turn_left"))
                .triggerableAnim("sit_down",
                        RawAnimation.begin().thenPlay("animation.frilled_drake.sitdown"))
                .triggerableAnim("lay_down",
                        RawAnimation.begin().thenPlay("animation.frilled_drake.laydown"))
                .triggerableAnim("wake_up",
                        RawAnimation.begin().thenPlay("animation.frilled_drake.wake_up"))
                .triggerableAnim("hatch",
                        RawAnimation.begin().thenPlay("animation.frilled_drake.hatch")));
    }
    // #endregion

    // #region Drake Meal
    @SuppressWarnings("null")
    @Override
    public InteractionResult interactAt(@Nonnull Player player, @Nonnull Vec3 vec, @Nonnull InteractionHand hand) {
        if (player.getItemInHand(hand).is(DragonoidsExpanded.DRAKE_MEAL)) {
            int growthScore = getGrowthScore();

            if (growthScore <= DrakeAge.HATCHLING.getAge()) {
                setGrowthScore(DrakeAge.HATCHLING.getAge() + 1);
                triggerAnim("defaultController", "hatch");
            } else if (growthScore <= DrakeAge.DRAKELING.getAge()) {
                setGrowthScore(DrakeAge.DRAKELING.getAge() + 1);
            } else if (growthScore <= DrakeAge.TEEN.getAge()) {
                setGrowthScore(DrakeAge.TEEN.getAge() + 1);
            } else if (growthScore <= DrakeAge.ADULT.getAge()) {
                setGrowthScore(DrakeAge.ADULT.getAge() + 1);
            } else if (growthScore <= DrakeAge.ELDER.getAge()) {
                setGrowthScore(DrakeAge.ELDER.getAge() + 1);
            } else if (growthScore <= DrakeAge.MAX_GROWTH.getAge()) {
                setGrowthScore(DrakeAge.MAX_GROWTH.getAge() + 1);
            } else if (growthScore <= DrakeAge.MAX_GROWTH.getAge()) {
                setGrowthScore(DrakeAge.MAX_GROWTH.getAge() + 1);
            }

            if (!player.isCreative()) {
                player.getItemInHand(hand).shrink(1);
            }

            return InteractionResult.CONSUME;
        } else if (isFood(player.getItemInHand(hand)) && getGrowthScore() >= DrakeAge.HATCHLING.getAge()) {
            if (getOwner() == null) {
                this.tame(player);
            }
            return InteractionResult.SUCCESS;
        } else if (player.getItemInHand(hand).is(Items.ROSE_BUSH) && getGrowthScore() >= DrakeAge.TEEN.getAge()) {
            this.setInLove(player);
            return InteractionResult.SUCCESS;
        } else if (getGrowthScore() >= DrakeAge.TEEN.getAge() && getOwner() != null && getOwner().is(player)) {
            doPlayerRide(player);
            return InteractionResult.SUCCESS;
        } else if (getGrowthScore() >= DrakeAge.HATCHLING.getAge()) {
            player.hurtServer(level().getServer().getLevel(level().dimension()), damageSources().mobAttack(this), 15f);
            if (level().isClientSide) {
                triggerAnim("attackController", "bite");
            }
            return InteractionResult.PASS;
        } else {
            return InteractionResult.PASS;
        }
    }
    // #endregion

    // #region ?
    @Override
    public SpawnGroupData finalizeSpawn(@Nonnull ServerLevelAccessor level, @Nonnull DifficultyInstance difficulty,
            @Nonnull EntitySpawnReason spawnReason, @Nullable SpawnGroupData spawnGroupData) {
        SpawnGroupData spawnGroupDataInternal = super.finalizeSpawn(level, difficulty, spawnReason, spawnGroupData);
        this.setColor(level().getRandom().nextInt(0, 4));
        setGrowthScore(0);
        return spawnGroupDataInternal;
    }
    // #endregion

    // #region Immunities
    @Override
    public boolean hurtServer(@Nonnull ServerLevel level, @Nonnull DamageSource source, float amount) {
        if (source.is(DamageTypes.DROWN) || source.is(DamageTypes.FALL))
            return false;
        return super.hurtServer(level, source, amount);
    }
    // #endregion

    // #region Help
    public static AttributeSupplier.Builder createMobAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 450.0).add(Attributes.ATTACK_DAMAGE, 10.0)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0)
                .add(Attributes.ATTACK_SPEED, 2.4).add(Attributes.FOLLOW_RANGE, 50.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3).add(Attributes.STEP_HEIGHT, 3)
                .add(Attributes.JUMP_STRENGTH, 5.0);
    }
    // #endregion

    // #region Serialization
    @Override
    public void addAdditionalSaveData(@Nonnull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("GrowthStage", getGrowthScore());
        compound.putInt("Color", getColor());
        compound.putFloat("AttackRange", getAttackRange());
    }

    @Override
    public void readAdditionalSaveData(@Nonnull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        setGrowthScore(compound.getInt("GrowthStage"));
        setColor(compound.getInt("Color"));
        setAttackRange(compound.getFloat("AttackRange"));
    }
    // #endregion

    @Override
    protected Brain.Provider<?> brainProvider() {
        return new SmartBrainProvider<>(this);
    }

    @Override
    protected PathNavigation createNavigation(@Nonnull Level pLevel) {
        SmoothAmphibiousPathNavigation navigation = new SmoothAmphibiousPathNavigation(this, pLevel) {
            @Override
            public boolean prefersShallowSwimming() {
                return false;
            }
        };

        return navigation;
    }

    /**
     *
     * public void aiStep() {
     * this.tickPart(this.head, Mth.cos(this.getYRot()), 1.4f,
     * Mth.sin(this.getYRot()));
     * this.tickPart(this.body, 0.0f, 0.0f, 0.0f);
     * }
     */

    private int timer = 0;

    // #region Ai Step
    @Override
    public void aiStep() {
        
        if (!level().isClientSide){ 
            if (getGrowthScore() >= DrakeAge.HATCHLING.getAge()) {
                if (getState() != DrakeState.SLEEPING.getState()) {
                    blinkTimer++;

                    if (blinking) {
                        if (blinkTimer > blinkTime) {
                            DragonoidsExpanded.LOGGER.info("I have stopped blinking.");
                            blinkTimer = 0;
                            blinking = false;
                            DragonoidsExpanded.LOGGER.info("Blinking: " + blinking);
                        }
                    } else if (blinkTimer > blinkDelay) {
                        DragonoidsExpanded.LOGGER.info("I have started blinking.");
                        blinkTimer = 0;
                        blinking = true;
                        DragonoidsExpanded.LOGGER.info("Blinking: " + blinking);
                    }
                }

                if (getState() != DrakeState.SLEEPING.getState()) {
                    timer--;

                    if (timer <= 0) {
                        timer = getRandom().nextIntBetweenInclusive(yawnDelayMin, yawnDelayMax);
                        triggerAnim("attackController", "yawn");
                        playSound(DragonoidsExpanded.FRILLED_DRAKE_YAWN.get(),
                                (0.5f + 0.5f * getGrowthScore() / DrakeAge.MAX_GROWTH.getAge()),
                                (1.5f - 0.75f * getGrowthScore() / DrakeAge.MAX_GROWTH.getAge()));
                    }
                }

                if (level().isNight() && !isAggressive() && getState() != DrakeState.SLEEPING.getState()) {
                    triggerAnim("defaultController", "lay_down");
                    setState(DrakeState.SLEEPING.getState());
                } else if ((!level().isNight() || isAggressive())
                        && getState() == DrakeState.SLEEPING.getState()) {
                    triggerAnim("defaultController", "wake_up");
                    setState(DrakeState.AWAKE.getState());
                }
            }
        }
        super.aiStep();
    }

    @Override
    protected void customServerAiStep(@Nonnull ServerLevel level) {
        if (getGrowthScore() >= DrakeAge.HATCHLING.getAge())
            tickBrain(this);
        if (getState() != DrakeState.SLEEPING.getState()) {
            blinkTimer++;

            if (blinking) {
                if (blinkTimer > blinkTime) {
                    DragonoidsExpanded.LOGGER.info("I have stopped blinking.");
                    blinkTimer = 0;
                    blinking = false;
                    DragonoidsExpanded.LOGGER.info("Blinking: " + blinking);
                }
            } else if (blinkTimer > blinkDelay) {
                DragonoidsExpanded.LOGGER.info("I have started blinking.");
                blinkTimer = 0;
                blinking = true;
                DragonoidsExpanded.LOGGER.info("Blinking: " + blinking);
            }
        }
    }
    // #endregion

    // #region Sensors
    @Override
    public List<ExtendedSensor<? extends FrilledDrake>> getSensors() {
        return ObjectArrayList.of(
                new NearbyLivingEntitySensor<FrilledDrake>()
                        .setPredicate((target,
                                entity) -> !(entity.getState() == DrakeState.SLEEPING.getState())), // This
                new NearbyAdultSensor<>(),
                new HurtBySensor<>(),
                new InWaterSensor<>()); // This tracks the last damage source and attacker
    }
    // #endregion

    // #region Tasks
    @Override
    public BrainActivityGroup<? extends FrilledDrake> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                new BreedWithPartner<FrilledDrake>().closeEnoughDist((entity, partner) -> 6)
                        .runFor((entity) -> 1200).whenStarting((entity) -> {
                            DragonoidsExpanded.LOGGER.info("They are now ready to breed.");
                        }).whenStopping((entity) -> {
                            DragonoidsExpanded.LOGGER.info("They are no longer ready to breed.");
                        })
                        .startCondition((entity) -> !(entity.getState() == DrakeState.SLEEPING.getState())),
                new LookAtTarget<FrilledDrake>()
                        .startCondition((entity) -> !(entity.getState() == DrakeState.SLEEPING.getState())),
                new MoveToWalkTarget<>()); // Walk towards
                                           // the current
                                           // walk target
    }

    @SuppressWarnings({ "unchecked", "null" })
    @Override
    public BrainActivityGroup<? extends FrilledDrake> getIdleTasks() { // These are the tasks that run when the mob
                                                                       // isn't doing anything else (usually)
        return BrainActivityGroup.idleTasks(
                new FirstApplicableBehaviour<>(
                        new TargetOrRetaliate<>().attackablePredicate(
                                (target) -> !(target instanceof FrilledDrake || target instanceof Creeper
                                        || getGrowthScore() < DrakeAge.DRAKELING.getAge()
                                        || target instanceof Bat
                                        || target instanceof GlowSquid
                                        || (this.getOwner() != null && this.getOwner().is(target))
                                        || (this.getOwner() != null && !(target instanceof Mob))
                                        || (target instanceof Player && ((Player) target).isCreative()))),
                        new SetPlayerLookTarget<>(),
                        new FollowOwner<>().teleportToTargetAfter(128).stopFollowingWithin(24)),
                new OneRandomBehaviour<>(
                        new SetRandomWalkTarget<FrilledDrake>().speedModifier(0.5f)
                                .startCondition((entity) -> !(entity.getState() == DrakeState.SLEEPING.getState())),
                        new Idle<>().runFor(entity -> entity.getRandom().nextInt(30, 60))));
    }

    @SuppressWarnings({ "unchecked", "null" })
    @Override
    public BrainActivityGroup<? extends FrilledDrake> getFightTasks() { // These are the tasks that handle fighting
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
                                    return (BrainUtil.getTargetOfEntity(entity) != null
                                            && BrainUtil.getTargetOfEntity(entity).distanceTo(entity) > 7);
                                }).cooldownFor((entity) -> 120), // Set the walk target to the attack target
                        new AnimatableMeleeAttack<>(12).whenStarting(entity -> {
                            setAggressive(true);
                            triggerAnim("attackController", "bite");
                        }).whenStopping(entity -> setAggressive(false))

                ));
    }
    // #endregion

    @Override
    public boolean isFood(@Nonnull ItemStack stack) {
        return stack.is(Items.BEEF);
    }

    @Override
    public boolean isWithinMeleeAttackRange(@Nonnull LivingEntity entity) {
        return this.getAttackBoundingBox().intersects(entity.getHitbox());
    }

    protected AABB getAttackBoundingBox() {
        Entity entity = this.getVehicle();
        AABB aabb;
        if (entity != null) {
            AABB aabb1 = entity.getBoundingBox();
            AABB aabb2 = this.getBoundingBox();
            aabb = new AABB(
                    Math.min(aabb2.minX, aabb1.minX),
                    aabb2.minY,
                    Math.min(aabb2.minZ, aabb1.minZ),
                    Math.max(aabb2.maxX, aabb1.maxX),
                    aabb2.maxY,
                    Math.max(aabb2.maxZ, aabb1.maxZ));
        } else {
            aabb = this.getBoundingBox();
        }

        return aabb.inflate(getAttackRange(), 0.0, getAttackRange());
    }

    @Override
    protected void tickRidden(@Nonnull Player player, @Nonnull Vec3 travelVector) {
        super.tickRidden(player, travelVector);
        Vec2 vec2 = this.getRiddenRotation(player);
        this.setRot(vec2.y, vec2.x);
        this.setYHeadRot(player.getYHeadRot());
        if (this.isControlledByLocalInstance()) {
            // Check if on ground to allow for jumping
            if (this.onGround()) {
                this.isJumping = false;
                if (this.playerJumpPendingScale > 0.0F && !this.isJumping) {
                    triggerAnim("defaultController", "jump");
                    this.executeRidersJump(this.playerJumpPendingScale, travelVector);
                }
                this.playerJumpPendingScale = 0.0F;
            }

            // Trigger turning animations based on player's camera rotation
            float currentYaw = this.getYRot();
            targetYaw = this.getYHeadRot();
            float deltaYaw = Mth.wrapDegrees(targetYaw - currentYaw);

            DragonoidsExpanded.LOGGER
                    .info("Current Yaw: " + currentYaw + "\nTarget Yaw: " + targetYaw + "\nDelta Yaw: " + deltaYaw);

            if (Math.abs(deltaYaw) > 5.0F) { // Only trigger animation for significant rotation
                if (deltaYaw < 0) {
                    // Player is turning right
                    triggerAnim("defaultController", "animation.frilled_drake.turn_right");
                } else {
                    // Player is turning left
                    triggerAnim("defaultController", "animation.frilled_drake.turn_left");
                }
            }

            this.setYRot(this.interpolateRotation(currentYaw, targetYaw, 5.0F)); // Smoothly rotate towards target yaw
        }
    }

    @Override
    public Vec3 getPassengerRidingPosition(@Nonnull Entity entity) {
        return super.getPassengerRidingPosition(entity).add(0, -0.35, 0);
    }

    protected Vec2 getRiddenRotation(LivingEntity entity) {
        return new Vec2(entity.getXRot() * 0.5F, entity.getYRot());
    }

    @Override
    protected Vec3 getRiddenInput(@Nonnull Player player, @Nonnull Vec3 travelVector) {
        float f = player.xxa * 0.5F;
        float f1 = player.zza;
        if (f1 <= 0.0F) {
            f1 *= 0.25F;
        }

        return new Vec3((double) f, 0.0, (double) f1);
    }

    @Override
    protected float getRiddenSpeed(@Nonnull Player player) {
        return (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED);
    }

    protected void executeRidersJump(float playerJumpPendingScale, Vec3 travelVector) {
        double d0 = (double) this.getJumpPower(playerJumpPendingScale);
        Vec3 vec3 = this.getDeltaMovement();
        this.setDeltaMovement(vec3.x, d0, vec3.z);
        this.isJumping = true;
        this.hasImpulse = true;
        net.neoforged.neoforge.common.CommonHooks.onLivingJump(this);
        if (travelVector.z > 0.0) {
            float f = Mth.sin(this.getYRot() * (float) (Math.PI / 180.0));
            float f1 = Mth.cos(this.getYRot() * (float) (Math.PI / 180.0));
            this.setDeltaMovement(this.getDeltaMovement().add((double) (-0.4F * f * playerJumpPendingScale), 0.0,
                    (double) (0.4F * f1 * playerJumpPendingScale)));
        }
    }

    @Override
    public void onPlayerJump(int jumpPower) {
        if (jumpPower < 0) {
            jumpPower = 0;
        } else {
        }

        if (jumpPower >= 90) {
            this.playerJumpPendingScale = 1.0F;
        } else {
            this.playerJumpPendingScale = 0.4F + 0.4F * (float) jumpPower / 90.0F;
        }
    }

    @Override
    public boolean canJump() {
        return true;
    }

    @Override
    public void handleStartJump(int jumpPower) {
    }

    @Override
    public void handleStopJump() {
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        Entity entity = this.getFirstPassenger();
        if (entity instanceof Player) {
            return (Player) entity;
        }

        return super.getControllingPassenger();
    }

    protected void doPlayerRide(Player player) {
        if (!this.level().isClientSide) {
            player.setYRot(this.getYRot());
            player.setXRot(this.getXRot());
            player.startRiding(this);
        }
    }
    // Coder no spell good.

    private float targetYaw;

    private float interpolateRotation(float currentYaw, float targetYaw, float maxStep) {
        float delta = Mth.wrapDegrees(targetYaw - currentYaw);
        if (delta > maxStep)
            delta = maxStep;
        if (delta < -maxStep)
            delta = -maxStep;
        return currentYaw + delta;
    }
}

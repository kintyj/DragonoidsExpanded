package com.kintyj.dragonoidexpanded.entity;

import java.util.EnumMap;
import java.util.List;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.InvalidateAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.TargetOrRetaliate;
import net.tslat.smartbrainlib.api.core.navigation.SmoothAmphibiousPathNavigation;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.InWaterSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.animation.RawAnimation;

public class FrilledDrake extends AgeableMob implements Enemy, GeoEntity, SmartBrainOwner<FrilledDrake> {
    public FrilledDrake(EntityType<? extends FrilledDrake> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);}
    
    private static final EntityDataAccessor<Integer> GROWTH_SCORE = SynchedEntityData.defineId(FrilledDrake.class, EntityDataSerializers.INT);
    private static final net.minecraft.network.syncher.EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(FrilledDrake.class, EntityDataSerializers.INT);

    public static final int COLOR_BLUE = 0;
    public static final int COLOR_AQUE = 1;
    public static final int COLOR_TURQUOISE = 2;
    public static final int COLOR_GREEN = 3;

    private static final int MAX_GROWTH = 1000;

    public int getGrowthScore() {
        return this.entityData.get(GROWTH_SCORE);
    }
    
    public void setGrowthScore(int pGrowthScore) {
        this.entityData.set(GROWTH_SCORE, pGrowthScore);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(GROWTH_SCORE, 0);
        builder.define(COLOR, COLOR_BLUE);
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide) {
            int growth = this.entityData.get(GROWTH_SCORE);

            // Increment growth score by 2 every day
            if (this.tickCount % 1 == 0 && growth < MAX_GROWTH) {
                growth += 1;
                this.entityData.set(GROWTH_SCORE, growth);
                updateScale(growth);
            }
        }

    }

    private void updateScale(int growth) {
        float scale = 0.1f + (growth / (float) MAX_GROWTH) * (5.0f - 0.1f);
        this.setScale(scale);
    }

    public void setScale(float scale) {
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(scale); // Example of scale affecting behavior
        this.refreshDimensions();
    }

    /** 
    @Override
    public ResourceLocation getTextureLocation() {
        int growth = this.entityData.get(GROWTH_SCORE);
        int color = this.entityData.get(COLOR);

        int stage;
        if (growth >= 301) stage = 3;
        else if (growth >= 201) stage = 2;
        else if (growth >= 101) stage = 1;
        else stage = 0;

        return TEXTURES.get(color)[stage];
    }
    */

    public void setColor(int color) {
        this.entityData.set(COLOR, color);
    }

    public int getColor() {
        return this.entityData.get(COLOR);
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob partner) {
        FrilledDrake child = (FrilledDrake) this.getType().create(serverLevel);
        if (child != null) {
            child.setColor(this.getColor());
        }
        return child;
    }

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
    
    @Override
    public void registerControllers(ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "defaultController", 3, event -> {
            return event.setAndContinue(event.isMoving() ? (this.isInWater() ? RawAnimation.begin().thenLoop("animation.frilled_drake.swim") : RawAnimation.begin().thenLoop("animation.frilled_drake.walk"))
             : (this.isInWater() ? RawAnimation.begin().thenLoop("animation.frilled_drake.float") : RawAnimation.begin().thenLoop("animation.frilled_drake.idle")));
        }));
    }

    // #region Immunities & Vulnerabilities
    @Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.is(DamageTypes.DROWN))
			return false;
		return super.hurt(source, amount);
	}
    //#endregion

    //#region Help
    public static AttributeSupplier.Builder createMobAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 450.0).add(Attributes.ATTACK_DAMAGE, 10.0).add(Attributes.ATTACK_KNOCKBACK, 1.0)
        .add(Attributes.ATTACK_SPEED, 2.4).add(Attributes.FOLLOW_RANGE, 50.0).add(Attributes.MOVEMENT_SPEED, 0.3);

    }
    //#endregion

    @Override
    protected Brain.Provider<?> brainProvider() {
        return new SmartBrainProvider<>(this);
    }

    @Override
    protected PathNavigation createNavigation(Level pLevel) {
        SmoothAmphibiousPathNavigation navigation = new SmoothAmphibiousPathNavigation(this, pLevel) {
            @Override
            public boolean prefersShallowSwimming() {
                return true;
            }
        };

        return navigation;
    }

    @Override
    protected void customServerAiStep() {
        tickBrain(this);
    }

    @Override
    public List<ExtendedSensor<? extends FrilledDrake>> getSensors() {
        return ObjectArrayList.of(new NearbyLivingEntitySensor<FrilledDrake>().setPredicate((target, entity) -> !(target instanceof FrilledDrake)), // This tracks nearby entities
                new HurtBySensor<>(), new InWaterSensor<>()); // This tracks the last damage source and attacker

    }

    @Override
    public BrainActivityGroup<? extends FrilledDrake> getCoreTasks() { // These are the tasks that run all the time
                                                                     // (usually)
        return BrainActivityGroup.coreTasks(new LookAtTarget<>(), // Have the entity turn to face and look at its current look target
                new MoveToWalkTarget<>()); // Walk towards the current walk target
    }

    @SuppressWarnings("unchecked") @Override
    public BrainActivityGroup<? extends FrilledDrake> getIdleTasks() { // These are the tasks that run when the mob isn't doing anything else (usually)
        return BrainActivityGroup.idleTasks(new FirstApplicableBehaviour<FrilledDrake>(new TargetOrRetaliate<>(), new SetPlayerLookTarget<>(), new SetRandomLookTarget<>()), new OneRandomBehaviour<>(new SetRandomWalkTarget<>().speedModifier(0.5f), new Idle<>().runFor(entity -> entity.getRandom().nextInt(30, 60))));
    }

    @Override
    public BrainActivityGroup<? extends FrilledDrake> getFightTasks() { // These are the tasks that handle fighting
        return BrainActivityGroup.fightTasks(new InvalidateAttackTarget<>(), // Cancel fighting if the target is no longer valid
                new SetWalkTargetToAttackTarget<>(), // Set the walk target to the attack target
                new AnimatableMeleeAttack<>(0).whenStarting(entity -> setAggressive(true)).whenStopping(entity -> setAggressive(false))
                );
    }
}

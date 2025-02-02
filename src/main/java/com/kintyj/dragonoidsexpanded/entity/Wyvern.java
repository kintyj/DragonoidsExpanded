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
import net.minecraft.world.entity.ai.navigation.PathNavigation;
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
import net.tslat.smartbrainlib.api.core.navigation.SmoothAmphibiousPathNavigation;
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
    
    //#region Base Stats        
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
    //#endregion

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
        
        controllers.add(new AnimationController<>(this, "wingController", 20, event -> {
            if (event.isMoving()) {
                return event.setAndContinue(
                (RawAnimation.begin().thenLoop("animation.wyvern.wings_folded")));
            } else { // Turning left
                return event.setAndContinue(RawAnimation.begin().thenPlay("animation.wyvern.wings_folded"));
            }

        }));
        controllers.add(new AnimationController<>(this, "bodyController", 10, event -> {
            if (event.isMoving()) {
                if (this.isAggressive()) {
                    return event.setAndContinue((RawAnimation.begin().thenLoop("animation.wyvern.idle_1")));
                } else {
                    return event.setAndContinue((RawAnimation.begin().thenLoop("animation.wyvern.idle_1")));   
                }
            } else { 
                return event.setAndContinue(RawAnimation.begin().thenPlay("animation.wyvern.idle_1"));
            }
        }));
    }
    //#endregion

    //#region Sensors
    @Override
    public List<ExtendedSensor<? extends Wyvern>> getSensors() {
        return ObjectArrayList.of(
                new NearbyLivingEntitySensor<Wyvern>(),
                new NearbyAdultSensor<>(),
                new HurtBySensor<>(),
                new NearbyItemsSensor<>());
    }
    //#endregion

    @Override
    protected void customServerAiStep() {
        tickBrain(this);
    }

    //#region Brains
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
                        new TargetOrRetaliate<>(),
                        new SetPlayerLookTarget<>(),
                        new FollowOwner<>().teleportToTargetAfter(128).stopFollowingWithin(24)),
                new OneRandomBehaviour<>(
                        new SetRandomWalkTarget<Wyvern>().speedModifier(0.5f),
                        new Idle<>().runFor(entity -> entity.getRandom().nextInt(30, 60))));
    }

    //#endregion

    @Override
    public boolean wantsToPickUp(ItemStack stack) {
        return stack.is(Items.IRON_INGOT);
    }

    public Wyvern(EntityType<? extends Wyvern> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
}

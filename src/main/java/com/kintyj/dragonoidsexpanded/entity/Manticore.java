package com.kintyj.dragonoidsexpanded.entity;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyAdultSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class Manticore extends TamableAnimal
        implements Enemy, GeoEntity, SmartBrainOwner<Manticore> {
    
    public static AttributeSupplier.Builder createMobAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 60.0)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0)
                .add(Attributes.ATTACK_SPEED, 2.4)
                .add(Attributes.FOLLOW_RANGE, 50.0)
                .add(Attributes.MOVEMENT_SPEED, 0.4);
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
        }).triggerableAnim("right_strike", RawAnimation.begin().thenPlay("animation.Manticore.right_strike")));
    }
    //#endregion

    @Override
    public List<ExtendedSensor<? extends Manticore>> getSensors() {
        return ObjectArrayList.of(
                new NearbyLivingEntitySensor<Manticore>(),
                new NearbyAdultSensor<>(),
                new HurtBySensor<>()); // This tracks the last damage source and attacker

    }

    public Manticore(EntityType<? extends Manticore> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
}

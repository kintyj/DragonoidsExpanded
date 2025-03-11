package com.kintyj.dragonoidsexpanded.effect;

import javax.annotation.Nonnull;

import com.kintyj.dragonoidsexpanded.DragonoidsExpanded;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;;

public class Mortis extends MobEffect {
    public Mortis() {
        super(MobEffectCategory.HARMFUL, 6358048);
        addAttributeModifier(Attributes.MAX_HEALTH, model ,AttributeModifier.Operation.ADD_VALUE, (level) -> -4 * (1 + level));
    }

    private static final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(DragonoidsExpanded.MODID,
            "no_idea_what_this_is");

    @Override
    public boolean applyEffectTick(@Nonnull ServerLevel level, @Nonnull LivingEntity p_294484_, int p_294672_) {
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int p_295357_, int p_294523_) {
        return true;
    }

    @Override
    public void onEffectStarted(@Nonnull LivingEntity p_294820_, int p_295222_) {
        super.onEffectStarted(p_294820_, p_295222_);
    }
}

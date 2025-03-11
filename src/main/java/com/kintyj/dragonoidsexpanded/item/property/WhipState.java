package com.kintyj.dragonoidsexpanded.item.property;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.kintyj.dragonoidsexpanded.DragonoidsExpanded;
import com.kintyj.dragonoidsexpanded.component.WhipStateComponent;
import com.kintyj.dragonoidsexpanded.item.Whip;
import com.mojang.serialization.MapCodec;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public record WhipState() implements RangeSelectItemModelProperty {
    public static final MapCodec<WhipState> MAP_CODEC = MapCodec.unit(new WhipState());

    @Override
    public float get(@Nonnull ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
        return (float) stack.getOrDefault(DragonoidsExpanded.WHIP_STATE, new WhipStateComponent(Whip.WhipState.IDLE.getState())).state();
    }

    @Override
    public MapCodec<WhipState> type() {
        return MAP_CODEC;
    }
}

package com.kintyj.dragonoidsexpanded.client.renderer.entity;

import com.kintyj.dragonoidsexpanded.client.renderer.entity.model.FrilledDrakeModel;
import com.kintyj.dragonoidsexpanded.entity.FrilledDrake;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class FrilledDrakeRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<FrilledDrake, R> {
    public FrilledDrakeRenderer(EntityRendererProvider.Context context) {
        super(context, new FrilledDrakeModel());
    }
} 

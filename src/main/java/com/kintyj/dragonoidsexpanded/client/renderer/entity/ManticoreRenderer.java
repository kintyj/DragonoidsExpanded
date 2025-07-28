package com.kintyj.dragonoidsexpanded.client.renderer.entity;

import com.kintyj.dragonoidsexpanded.client.renderer.entity.model.ManticoreModel;
import com.kintyj.dragonoidsexpanded.entity.Manticore;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class ManticoreRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<Manticore, R> {
    public ManticoreRenderer(EntityRendererProvider.Context context) {
        super(context, new ManticoreModel());
    }
}
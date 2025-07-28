package com.kintyj.dragonoidsexpanded.client.renderer.entity;

import com.kintyj.dragonoidsexpanded.client.renderer.entity.model.WyvernModel;
import com.kintyj.dragonoidsexpanded.entity.Wyvern;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class WyvernRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<Wyvern, R> {
    public WyvernRenderer(EntityRendererProvider.Context context) {
        super(context, new WyvernModel());
    }
}
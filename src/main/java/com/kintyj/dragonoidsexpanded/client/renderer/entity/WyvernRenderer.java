package com.kintyj.dragonoidsexpanded.client.renderer.entity;

import com.kintyj.dragonoidsexpanded.client.renderer.entity.model.WyvernModel;
import com.kintyj.dragonoidsexpanded.entity.Wyvern;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class WyvernRenderer extends GeoEntityRenderer<Wyvern> {
    public WyvernRenderer(EntityRendererProvider.Context context) {
        super(context, new WyvernModel());
    }
}
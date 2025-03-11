package com.kintyj.dragonoidsexpanded.client.renderer.entity;

import com.kintyj.dragonoidsexpanded.client.renderer.entity.model.ManticoreModel;
import com.kintyj.dragonoidsexpanded.entity.Manticore;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ManticoreRenderer extends GeoEntityRenderer<Manticore> {
    public ManticoreRenderer(EntityRendererProvider.Context context) {
        super(context, new ManticoreModel());
    }
}
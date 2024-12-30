package com.kintyj.dragonoidexpanded.client.renderer.entity;

import com.kintyj.dragonoidexpanded.client.renderer.entity.model.FrilledDrakeModel;
import com.kintyj.dragonoidexpanded.entity.FrilledDrake;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FrilledDrakeRenderer extends GeoEntityRenderer<FrilledDrake>{
    public FrilledDrakeRenderer(EntityRendererProvider.Context context) {
        super(context, new FrilledDrakeModel());
    }
}

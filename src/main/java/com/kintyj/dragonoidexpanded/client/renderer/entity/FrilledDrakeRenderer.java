package com.kintyj.dragonoidexpanded.client.renderer.entity;

import com.kintyj.dragonoidexpanded.client.renderer.entity.model.FrilledDrakeModel;
import com.kintyj.dragonoidexpanded.entity.FrilledDrake;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FrilledDrakeRenderer extends GeoEntityRenderer<FrilledDrake> {
    public FrilledDrakeRenderer(EntityRendererProvider.Context context) {
        super(context, new FrilledDrakeModel());
    }

    @Override
    public void render(FrilledDrake entity, float entityYaw, float partialTick, PoseStack poseStack,
            MultiBufferSource bufferSource, int packedLight) {
        entityYaw = entity.getYRot();
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}

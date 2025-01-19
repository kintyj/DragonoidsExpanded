package com.kintyj.dragonoidsexpanded.client.renderer.entity;

import com.kintyj.dragonoidsexpanded.client.renderer.entity.model.ManticoreModel;
import com.kintyj.dragonoidsexpanded.entity.Manticore;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ManticoreRenderer extends GeoEntityRenderer<Manticore> {
    public ManticoreRenderer(EntityRendererProvider.Context context) {
        super(context, new ManticoreModel());
    }

    @Override
    public void render(Manticore entity, float entityYaw, float partialTick, PoseStack poseStack,
            MultiBufferSource bufferSource, int packedLight) {
        entityYaw = entity.getYRot();
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
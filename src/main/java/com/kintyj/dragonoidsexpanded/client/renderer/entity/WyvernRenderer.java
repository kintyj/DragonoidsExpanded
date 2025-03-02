package com.kintyj.dragonoidsexpanded.client.renderer.entity;

import com.kintyj.dragonoidsexpanded.client.renderer.entity.model.WyvernModel;
import com.kintyj.dragonoidsexpanded.entity.Wyvern;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class WyvernRenderer extends GeoEntityRenderer<Wyvern> {
    public WyvernRenderer(EntityRendererProvider.Context context) {
        super(context, new WyvernModel());
    }

    @Override
    public void render(Wyvern entity, float entityYaw, float partialTick, PoseStack poseStack,
            MultiBufferSource bufferSource, int packedLight) {
        entityYaw = entity.getYRot();

        //ShaderInstance shader = DragonoidsExpandedShaders.doppleganger();

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
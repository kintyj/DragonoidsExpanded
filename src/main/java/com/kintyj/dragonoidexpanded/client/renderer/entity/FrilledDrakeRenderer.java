package com.kintyj.dragonoidexpanded.client.renderer.entity;

import com.kintyj.dragonoidexpanded.DragonoidExpanded;
import com.kintyj.dragonoidexpanded.entity.FrilledDrake;
import com.kintyj.dragonoidexpanded.client.renderer.entity.model.FrilledDrakeModel;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;

public class FrilledDrakeRenderer extends MobRenderer<FrilledDrake, FrilledDrakeModel> {

    public FrilledDrakeRenderer(EntityRendererProvider.Context context) {
        super(context, new FrilledDrakeModel(), 0.5f); // 0.5f is the shadow size, adjust as needed
    }

    @Override
    public void render(FrilledDrake entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
        // Update entityYaw to always match the look direction
        entityYaw = entity.getYRot();

        // Call the super method to apply the rendering
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(FrilledDrake entity) {
        // Return the appropriate texture resource for the entity
        if (entity.getGrowthScore() > FrilledDrake.DrakeAge.ELDER.getAge()) {
            return FrilledDrakeModel.texture[entity.getColor()][4]; // 401-402 days - Elder
        } else if (entity.getGrowthScore() > FrilledDrake.DrakeAge.ADULT.getAge()) {
            return FrilledDrakeModel.texture[entity.getColor()][3]; // 301-400 days - Adult
        } else if (entity.getGrowthScore() > FrilledDrake.DrakeAge.TEEN.getAge()) {
            return FrilledDrakeModel.texture[entity.getColor()][2]; // 101-300 days - Teen
        } else if (entity.getGrowthScore() > FrilledDrake.DrakeAge.DRAKELING.getAge()) {
            return FrilledDrakeModel.texture[entity.getColor()][1]; // 021-100 days - Drakeling
        } else {
            return FrilledDrakeModel.texture[entity.getColor()][0]; // 000-020 days - Hatchling
        }
    }
}

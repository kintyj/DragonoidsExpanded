package com.kintyj.dragonoidsexpanded.client.renderer.entity.model;

import com.kintyj.dragonoidsexpanded.DragonoidsExpanded;
import com.kintyj.dragonoidsexpanded.entity.Manticore;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;

public class ManticoreModel extends GeoModel<Manticore> {
	// Models must be stored in assets/<modid>/geo with subfolders supported inside
	// the geo folder
	private static final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(DragonoidsExpanded.MODID,
			"entity/manticore");
	// Textures must be stored in assets/<modid>/geo with subfolders supported
	// inside the textures folder
	private static final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(DragonoidsExpanded.MODID,
			"textures/entity/manticore/manticore_one.png");
	// Animations must be stored in assets/<modid>/animations with subfolders
	// supported inside the animations folder
	private static final ResourceLocation animation = ResourceLocation.fromNamespaceAndPath(
			DragonoidsExpanded.MODID,
			"entity/manticore");

	@Override
	public ResourceLocation getModelResource(GeoRenderState renderState) {
		return ManticoreModel.model;
	}

	@Override
	public ResourceLocation getAnimationResource(Manticore animatable) {
		return ManticoreModel.animation;
	}

	@Override
	public ResourceLocation getTextureResource(GeoRenderState renderState) {
		return ManticoreModel.texture;
	}
}

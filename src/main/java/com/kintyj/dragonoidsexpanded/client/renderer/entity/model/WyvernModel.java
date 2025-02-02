package com.kintyj.dragonoidsexpanded.client.renderer.entity.model;

import com.kintyj.dragonoidsexpanded.DragonoidsExpanded;
import com.kintyj.dragonoidsexpanded.entity.Wyvern;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class WyvernModel extends GeoModel<Wyvern> {
	// Models must be stored in assets/<modid>/geo with subfolders supported inside
	// the geo folder
	private static final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(DragonoidsExpanded.MODID,
			"geo/entity/wyvern.geo.json");
	// Textures must be stored in assets/<modid>/geo with subfolders supported
	// inside the textures folder
	private static final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(DragonoidsExpanded.MODID,
			"textures/entity/wyvern/common_wyvern_grayscale.png");
	// Animations must be stored in assets/<modid>/animations with subfolders
	// supported inside the animations folder
	private static final ResourceLocation animation = ResourceLocation.fromNamespaceAndPath(
			DragonoidsExpanded.MODID,
			"animations/entity/wyvern.animation.json");

	@Override
	public ResourceLocation getModelResource(Wyvern object) {
		return WyvernModel.model;
	}

	@Override
	public ResourceLocation getTextureResource(Wyvern object) {
		return WyvernModel.texture;
	}

	@Override
	public ResourceLocation getAnimationResource(Wyvern object) {
		return WyvernModel.animation;
	}
}
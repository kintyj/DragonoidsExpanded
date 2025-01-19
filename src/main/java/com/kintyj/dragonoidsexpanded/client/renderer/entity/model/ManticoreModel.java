package com.kintyj.dragonoidsexpanded.client.renderer.entity.model;

import com.kintyj.dragonoidsexpanded.DragonoidsExpanded;
import com.kintyj.dragonoidsexpanded.entity.FrilledDrake;
import com.kintyj.dragonoidsexpanded.entity.Manticore;
import com.kintyj.dragonoidsexpanded.entity.FrilledDrake.DrakeState;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ManticoreModel extends GeoModel<Manticore> {
	// Models must be stored in assets/<modid>/geo with subfolders supported inside
	// the geo folder
	private static final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(DragonoidsExpanded.MODID,
			"geo/entity/manticore.geo.json");
	// Textures must be stored in assets/<modid>/geo with subfolders supported
	// inside the textures folder
	private static final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(DragonoidsExpanded.MODID,
			"textures/entity/manticore/manticore_one.png");
	// Animations must be stored in assets/<modid>/animations with subfolders
	// supported inside the animations folder
	private static final ResourceLocation animation = ResourceLocation.fromNamespaceAndPath(
			DragonoidsExpanded.MODID,
			"animations/entity/manticore.animation.json");

	@Override
	public ResourceLocation getModelResource(Manticore object) {
		return ManticoreModel.model;
	}

	@Override
	public ResourceLocation getTextureResource(Manticore object) {
		return ManticoreModel.texture; // You have to sync to update github. Otherwise it's just on your computer. >:(
	}

	@Override
	public ResourceLocation getAnimationResource(Manticore object) {
		return ManticoreModel.animation;
	}
}

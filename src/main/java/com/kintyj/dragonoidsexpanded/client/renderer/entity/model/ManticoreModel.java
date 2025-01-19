package com.kintyj.dragonoidsexpanded.client.renderer.entity.model;

import com.kintyj.dragonoidsexpanded.DragonoidsExpanded;
import com.kintyj.dragonoidsexpanded.entity.FrilledDrake;
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
			"textures/entity/manticore.png");
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
		if (object.getGrowthScore() > FrilledDrake.DrakeAge.ELDER.getAge()) {
			return (object.isBlinking() || object.getState() == DrakeState.SLEEPING.getState())
					? FrilledDrakeModel.closedTexture[object.getColor()][4]
					: FrilledDrakeModel.texture[object.getColor()][4]; // 401-402 days - Elder
		} else if (object.getGrowthScore() > FrilledDrake.DrakeAge.ADULT.getAge()) {
			return (object.isBlinking() || object.getState() == DrakeState.SLEEPING.getState())
					? FrilledDrakeModel.closedTexture[object.getColor()][3]
					: FrilledDrakeModel.texture[object.getColor()][3]; // 301-400 days - Adult
		} else if (object.getGrowthScore() > FrilledDrake.DrakeAge.TEEN.getAge()) {
			return (object.isBlinking() || object.getState() == DrakeState.SLEEPING.getState())
					? FrilledDrakeModel.closedTexture[object.getColor()][2]
					: FrilledDrakeModel.texture[object.getColor()][2]; // 101-300 days - Teen
		} else if (object.getGrowthScore() > FrilledDrake.DrakeAge.DRAKELING.getAge()) {
			return (object.isBlinking() || object.getState() == DrakeState.SLEEPING.getState())
					? FrilledDrakeModel.closedTexture[object.getColor()][1]
					: FrilledDrakeModel.texture[object.getColor()][1]; // 021-100 days - Drakeling
		} else {
			return (object.isBlinking() || object.getState() == DrakeState.SLEEPING.getState())
					? FrilledDrakeModel.closedTexture[object.getColor()][0]
					: FrilledDrakeModel.texture[object.getColor()][0]; // 000-020 days - Hatchling
		}
	}

	@Override
	public ResourceLocation getAnimationResource(FrilledDrake object) {
		return FrilledDrakeModel.animation;
	}
}

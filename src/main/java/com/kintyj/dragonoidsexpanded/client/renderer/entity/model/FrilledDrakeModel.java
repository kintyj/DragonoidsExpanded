package com.kintyj.dragonoidsexpanded.client.renderer.entity.model;

import com.kintyj.dragonoidsexpanded.DragonoidsExpanded;
import com.kintyj.dragonoidsexpanded.entity.FrilledDrake;
import com.kintyj.dragonoidsexpanded.entity.FrilledDrake.DrakeState;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;

public class FrilledDrakeModel extends GeoModel<FrilledDrake> {
	// Models must be stored in assets/<modid>/geo with subfolders supported inside
	// the geo folder
	private static final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(DragonoidsExpanded.MODID,
			"entity/frilled_drake");
	// Textures must be stored in assets/<modid>/geo with subfolders supported
	// inside the textures folder
	private static final ResourceLocation[][] texture = {
			{ // Blue
					ResourceLocation.fromNamespaceAndPath(DragonoidsExpanded.MODID,
							"textures/entity/frilled_drake/frilled_drake_hatchling_one.png"),
					ResourceLocation.fromNamespaceAndPath(DragonoidsExpanded.MODID,
							"textures/entity/frilled_drake/frilled_drake_drakeling_one.png"),
					ResourceLocation.fromNamespaceAndPath(DragonoidsExpanded.MODID,
							"textures/entity/frilled_drake/frilled_drake_teen_one.png"),
					ResourceLocation.fromNamespaceAndPath(DragonoidsExpanded.MODID,
							"textures/entity/frilled_drake/frilled_drake_adult_one.png"),
					ResourceLocation.fromNamespaceAndPath(DragonoidsExpanded.MODID,
							"textures/entity/frilled_drake/frilled_drake_elder_one.png"),
			},
			{ // Aqua
					ResourceLocation.fromNamespaceAndPath(DragonoidsExpanded.MODID,
							"textures/entity/frilled_drake/frilled_drake_hatchling_two.png"),
					ResourceLocation.fromNamespaceAndPath(DragonoidsExpanded.MODID,
							"textures/entity/frilled_drake/frilled_drake_drakeling_two.png"),
					ResourceLocation.fromNamespaceAndPath(DragonoidsExpanded.MODID,
							"textures/entity/frilled_drake/frilled_drake_teen_two.png"),
					ResourceLocation.fromNamespaceAndPath(DragonoidsExpanded.MODID,
							"textures/entity/frilled_drake/frilled_drake_adult_two.png"),
					ResourceLocation.fromNamespaceAndPath(DragonoidsExpanded.MODID,
							"textures/entity/frilled_drake/frilled_drake_elder_two.png"),
			},
			{ // Turquoise
					ResourceLocation.fromNamespaceAndPath(DragonoidsExpanded.MODID,
							"textures/entity/frilled_drake/frilled_drake_hatchling_three.png"),
					ResourceLocation.fromNamespaceAndPath(DragonoidsExpanded.MODID,
							"textures/entity/frilled_drake/frilled_drake_drakeling_three.png"),
					ResourceLocation.fromNamespaceAndPath(DragonoidsExpanded.MODID,
							"textures/entity/frilled_drake/frilled_drake_teen_three.png"),
					ResourceLocation.fromNamespaceAndPath(DragonoidsExpanded.MODID,
							"textures/entity/frilled_drake/frilled_drake_adult_three.png"),
					ResourceLocation.fromNamespaceAndPath(DragonoidsExpanded.MODID,
							"textures/entity/frilled_drake/frilled_drake_elder_three.png"),
			},
			{ // Green
					ResourceLocation.fromNamespaceAndPath(DragonoidsExpanded.MODID,
							"textures/entity/frilled_drake/frilled_drake_hatchling_four.png"),
					ResourceLocation.fromNamespaceAndPath(DragonoidsExpanded.MODID,
							"textures/entity/frilled_drake/frilled_drake_drakeling_four.png"),
					ResourceLocation.fromNamespaceAndPath(DragonoidsExpanded.MODID,
							"textures/entity/frilled_drake/frilled_drake_teen_four.png"),
					ResourceLocation.fromNamespaceAndPath(DragonoidsExpanded.MODID,
							"textures/entity/frilled_drake/frilled_drake_adult_four.png"),
					ResourceLocation.fromNamespaceAndPath(DragonoidsExpanded.MODID,
							"textures/entity/frilled_drake/frilled_drake_elder_four.png"),
			}
	};
	private static final ResourceLocation[][] closedTexture = {
			{ // Blue
					ResourceLocation.fromNamespaceAndPath(DragonoidsExpanded.MODID,
							"textures/entity/frilled_drake/frilled_drake_hatchling_one_eyes_closed.png"),
					ResourceLocation.fromNamespaceAndPath(DragonoidsExpanded.MODID,
							"textures/entity/frilled_drake/frilled_drake_drakeling_one_eyes_closed.png"),
					ResourceLocation.fromNamespaceAndPath(DragonoidsExpanded.MODID,
							"textures/entity/frilled_drake/frilled_drake_teen_one_eyes_closed.png"),
					ResourceLocation.fromNamespaceAndPath(DragonoidsExpanded.MODID,
							"textures/entity/frilled_drake/frilled_drake_adult_one_eyes_closed.png"),
					ResourceLocation.fromNamespaceAndPath(DragonoidsExpanded.MODID,
							"textures/entity/frilled_drake/frilled_drake_elder_one_eyes_closed.png"),
			},
			{ // Aqua
					ResourceLocation.fromNamespaceAndPath(DragonoidsExpanded.MODID,
							"textures/entity/frilled_drake/frilled_drake_hatchling_two_eyes_closed.png"),
					ResourceLocation.fromNamespaceAndPath(DragonoidsExpanded.MODID,
							"textures/entity/frilled_drake/frilled_drake_drakeling_two.png"),
					ResourceLocation.fromNamespaceAndPath(DragonoidsExpanded.MODID,
							"textures/entity/frilled_drake/frilled_drake_teen_two_eyes_closed.png"),
					ResourceLocation.fromNamespaceAndPath(DragonoidsExpanded.MODID,
							"textures/entity/frilled_drake/frilled_drake_adult_two_eyes_closed.png"),
					ResourceLocation.fromNamespaceAndPath(DragonoidsExpanded.MODID,
							"textures/entity/frilled_drake/frilled_drake_elder_two_eyes_closed.png"),
			},
			{ // Turquoise
					ResourceLocation.fromNamespaceAndPath(DragonoidsExpanded.MODID,
							"textures/entity/frilled_drake/frilled_drake_hatchling_three_eyes_closed.png"),
					ResourceLocation.fromNamespaceAndPath(DragonoidsExpanded.MODID,
							"textures/entity/frilled_drake/frilled_drake_drakeling_three_eyes_closed.png"),
					ResourceLocation.fromNamespaceAndPath(DragonoidsExpanded.MODID,
							"textures/entity/frilled_drake/frilled_drake_teen_three_eyes_closed.png"),
					ResourceLocation.fromNamespaceAndPath(DragonoidsExpanded.MODID,
							"textures/entity/frilled_drake/frilled_drake_adult_three_eyes_closed.png"),
					ResourceLocation.fromNamespaceAndPath(DragonoidsExpanded.MODID,
							"textures/entity/frilled_drake/frilled_drake_elder_three_eyes_closed.png"),
			},
			{ // Green
					ResourceLocation.fromNamespaceAndPath(DragonoidsExpanded.MODID,
							"textures/entity/frilled_drake/frilled_drake_hatchling_four_eyes_closed.png"),
					ResourceLocation.fromNamespaceAndPath(DragonoidsExpanded.MODID,
							"textures/entity/frilled_drake/frilled_drake_drakeling_four_eyes_closed.png"),
					ResourceLocation.fromNamespaceAndPath(DragonoidsExpanded.MODID,
							"textures/entity/frilled_drake/frilled_drake_teen_four_eyes_closed.png"),
					ResourceLocation.fromNamespaceAndPath(DragonoidsExpanded.MODID,
							"textures/entity/frilled_drake/frilled_drake_adult_four_eyes_closed.png"),
					ResourceLocation.fromNamespaceAndPath(DragonoidsExpanded.MODID,
							"textures/entity/frilled_drake/frilled_drake_elder_four_eyes_closed.png"),
			}
	};
	// Animations must be stored in assets/<modid>/animations with subfolders
	// supported inside the animations folder
	private static final ResourceLocation animation = ResourceLocation.fromNamespaceAndPath(
			DragonoidsExpanded.MODID,
			"entity/frilled_drake");

	@Override
	public void addAdditionalStateData(FrilledDrake animatable, GeoRenderState renderState) {
		renderState.addGeckolibData(FrilledDrake.IS_BLINKING, animatable.isBlinking());
		renderState.addGeckolibData(FrilledDrake.ANIM_GROWTH_SCORE, animatable.getGrowthScore());
		renderState.addGeckolibData(FrilledDrake.ANIM_STATE, animatable.getState());
		renderState.addGeckolibData(FrilledDrake.ANIM_COLOR, animatable.getColor());
	}

	@Override
	public ResourceLocation getModelResource(GeoRenderState renderState) {
		return FrilledDrakeModel.model;
	}

	@Override
	public ResourceLocation getAnimationResource(FrilledDrake animatable) {
		return FrilledDrakeModel.animation;
	}

	@Override
	public ResourceLocation getTextureResource(GeoRenderState renderState) {
		if (renderState.getGeckolibData(FrilledDrake.ANIM_GROWTH_SCORE) > FrilledDrake.DrakeAge.ELDER.getAge()) {
			return (renderState.getGeckolibData(FrilledDrake.IS_BLINKING) || renderState.getGeckolibData(FrilledDrake.ANIM_STATE) == DrakeState.SLEEPING.getState())
					? FrilledDrakeModel.closedTexture[renderState.getGeckolibData(FrilledDrake.ANIM_COLOR)][4]
					: FrilledDrakeModel.texture[renderState.getGeckolibData(FrilledDrake.ANIM_COLOR)][4]; // 401-402 days - Elder
		} else if (renderState.getGeckolibData(FrilledDrake.ANIM_GROWTH_SCORE) > FrilledDrake.DrakeAge.ADULT.getAge()) {
			return (renderState.getGeckolibData(FrilledDrake.IS_BLINKING) || renderState.getGeckolibData(FrilledDrake.ANIM_STATE) == DrakeState.SLEEPING.getState())
					? FrilledDrakeModel.closedTexture[renderState.getGeckolibData(FrilledDrake.ANIM_COLOR)][3]
					: FrilledDrakeModel.texture[renderState.getGeckolibData(FrilledDrake.ANIM_COLOR)][3]; // 301-400 days - Adult
		} else if (renderState.getGeckolibData(FrilledDrake.ANIM_GROWTH_SCORE) > FrilledDrake.DrakeAge.TEEN.getAge()) {
			return (renderState.getGeckolibData(FrilledDrake.IS_BLINKING) || renderState.getGeckolibData(FrilledDrake.ANIM_STATE) == DrakeState.SLEEPING.getState())
					? FrilledDrakeModel.closedTexture[renderState.getGeckolibData(FrilledDrake.ANIM_COLOR)][2]
					: FrilledDrakeModel.texture[renderState.getGeckolibData(FrilledDrake.ANIM_COLOR)][2]; // 101-300 days - Teen
		} else if (renderState.getGeckolibData(FrilledDrake.ANIM_GROWTH_SCORE) > FrilledDrake.DrakeAge.DRAKELING.getAge()) {
			return (renderState.getGeckolibData(FrilledDrake.IS_BLINKING) || renderState.getGeckolibData(FrilledDrake.ANIM_STATE) == DrakeState.SLEEPING.getState())
					? FrilledDrakeModel.closedTexture[renderState.getGeckolibData(FrilledDrake.ANIM_COLOR)][1]
					: FrilledDrakeModel.texture[renderState.getGeckolibData(FrilledDrake.ANIM_COLOR)][1]; // 021-100 days - Drakeling
		} else {
			return (renderState.getGeckolibData(FrilledDrake.IS_BLINKING) || renderState.getGeckolibData(FrilledDrake.ANIM_STATE) == DrakeState.SLEEPING.getState())
					? FrilledDrakeModel.closedTexture[renderState.getGeckolibData(FrilledDrake.ANIM_COLOR)][0]
					: FrilledDrakeModel.texture[renderState.getGeckolibData(FrilledDrake.ANIM_COLOR)][0]; // 000-020 days - Hatchling
		}
	}
}

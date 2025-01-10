package com.kintyj.dragonoidexpanded.client.renderer.entity.model;

import com.kintyj.dragonoidexpanded.DragonoidExpanded;
import com.kintyj.dragonoidexpanded.entity.FrilledDrake;
import com.kintyj.dragonoidexpanded.entity.FrilledDrake.DrakeState;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FrilledDrakeModel extends GeoModel<FrilledDrake> {
        // Models must be stored in assets/<modid>/geo with subfolders supported inside
        // the geo folder
        private static final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(DragonoidExpanded.MODID,
                        "geo/frilled_drake.geo.json");
        // Textures must be stored in assets/<modid>/geo with subfolders supported
        // inside the textures folder
        private static final ResourceLocation[][] texture = {
                        { // Blue
                                        ResourceLocation.fromNamespaceAndPath(DragonoidExpanded.MODID,
                                                        "textures/entity/frilled_drake_hatchling_one.png"),
                                        ResourceLocation.fromNamespaceAndPath(DragonoidExpanded.MODID,
                                                        "textures/entity/frilled_drake_drakeling_one.png"),
                                        ResourceLocation.fromNamespaceAndPath(DragonoidExpanded.MODID,
                                                        "textures/entity/frilled_drake_teen_one.png"),
                                        ResourceLocation.fromNamespaceAndPath(DragonoidExpanded.MODID,
                                                        "textures/entity/frilled_drake_adult_one.png"),
                                        ResourceLocation.fromNamespaceAndPath(DragonoidExpanded.MODID,
                                                        "textures/entity/frilled_drake_elder_one.png"),
                        },
                        { // Aqua
                                        ResourceLocation.fromNamespaceAndPath(DragonoidExpanded.MODID,
                                                        "textures/entity/frilled_drake_hatchling_two.png"),
                                        ResourceLocation.fromNamespaceAndPath(DragonoidExpanded.MODID,
                                                        "textures/entity/frilled_drake_drakeling_two.png"),
                                        ResourceLocation.fromNamespaceAndPath(DragonoidExpanded.MODID,
                                                        "textures/entity/frilled_drake_teen_two.png"),
                                        ResourceLocation.fromNamespaceAndPath(DragonoidExpanded.MODID,
                                                        "textures/entity/frilled_drake_adult_two.png"),
                                        ResourceLocation.fromNamespaceAndPath(DragonoidExpanded.MODID,
                                                        "textures/entity/frilled_drake_elder_two.png"),
                        },
                        { // Turquoise
                                        ResourceLocation.fromNamespaceAndPath(DragonoidExpanded.MODID,
                                                        "textures/entity/frilled_drake_hatchling_three.png"),
                                        ResourceLocation.fromNamespaceAndPath(DragonoidExpanded.MODID,
                                                        "textures/entity/frilled_drake_drakeling_three.png"),
                                        ResourceLocation.fromNamespaceAndPath(DragonoidExpanded.MODID,
                                                        "textures/entity/frilled_drake_teen_three.png"),
                                        ResourceLocation.fromNamespaceAndPath(DragonoidExpanded.MODID,
                                                        "textures/entity/frilled_drake_adult_three.png"),
                                        ResourceLocation.fromNamespaceAndPath(DragonoidExpanded.MODID,
                                                        "textures/entity/frilled_drake_elder_three.png"),
                        },
                        { // Green
                                        ResourceLocation.fromNamespaceAndPath(DragonoidExpanded.MODID,
                                                        "textures/entity/frilled_drake_hatchling_four.png"),
                                        ResourceLocation.fromNamespaceAndPath(DragonoidExpanded.MODID,
                                                        "textures/entity/frilled_drake_drakeling_four.png"),
                                        ResourceLocation.fromNamespaceAndPath(DragonoidExpanded.MODID,
                                                        "textures/entity/frilled_drake_teen_four.png"),
                                        ResourceLocation.fromNamespaceAndPath(DragonoidExpanded.MODID,
                                                        "textures/entity/frilled_drake_adult_four.png"),
                                        ResourceLocation.fromNamespaceAndPath(DragonoidExpanded.MODID,
                                                        "textures/entity/frilled_drake_elder_four.png"),
                        }
        };
        private static final ResourceLocation[][] closedTexture = {
                        { // Blue
                                        ResourceLocation.fromNamespaceAndPath(DragonoidExpanded.MODID,
                                                        "textures/entity/frilled_drake_hatchling_one_eyes_closed.png"),
                                        ResourceLocation.fromNamespaceAndPath(DragonoidExpanded.MODID,
                                                        "textures/entity/frilled_drake_drakeling_one_eyes_closed.png"),
                                        ResourceLocation.fromNamespaceAndPath(DragonoidExpanded.MODID,
                                                        "textures/entity/frilled_drake_teen_one_eyes_closed.png"),
                                        ResourceLocation.fromNamespaceAndPath(DragonoidExpanded.MODID,
                                                        "textures/entity/frilled_drake_adult_one_eyes_closed.png"),
                                        ResourceLocation.fromNamespaceAndPath(DragonoidExpanded.MODID,
                                                        "textures/entity/frilled_drake_elder_one_eyes_closed.png"),
                        },
                        { // Aqua
                                        ResourceLocation.fromNamespaceAndPath(DragonoidExpanded.MODID,
                                                        "textures/entity/frilled_drake_hatchling_two_eyes_closed.png"),
                                        ResourceLocation.fromNamespaceAndPath(DragonoidExpanded.MODID,
                                                        "textures/entity/frilled_drake_drakeling_two.png"),
                                        ResourceLocation.fromNamespaceAndPath(DragonoidExpanded.MODID,
                                                        "textures/entity/frilled_drake_teen_two_eyes_closed.png"),
                                        ResourceLocation.fromNamespaceAndPath(DragonoidExpanded.MODID,
                                                        "textures/entity/frilled_drake_adult_two_eyes_closed.png"),
                                        ResourceLocation.fromNamespaceAndPath(DragonoidExpanded.MODID,
                                                        "textures/entity/frilled_drake_elder_two_eyes_closed.png"),
                        },
                        { // Turquoise
                                        ResourceLocation.fromNamespaceAndPath(DragonoidExpanded.MODID,
                                                        "textures/entity/frilled_drake_hatchling_three_eyes_closed.png"),
                                        ResourceLocation.fromNamespaceAndPath(DragonoidExpanded.MODID,
                                                        "textures/entity/frilled_drake_drakeling_three_eyes_closed.png"),
                                        ResourceLocation.fromNamespaceAndPath(DragonoidExpanded.MODID,
                                                        "textures/entity/frilled_drake_teen_three_eyes_closed.png"),
                                        ResourceLocation.fromNamespaceAndPath(DragonoidExpanded.MODID,
                                                        "textures/entity/frilled_drake_adult_three_eyes_closed.png"),
                                        ResourceLocation.fromNamespaceAndPath(DragonoidExpanded.MODID,
                                                        "textures/entity/frilled_drake_elder_three_eyes_closed.png"),
                        },
                        { // Green
                                        ResourceLocation.fromNamespaceAndPath(DragonoidExpanded.MODID,
                                                        "textures/entity/frilled_drake_hatchling_four_eyes_closed.png"),
                                        ResourceLocation.fromNamespaceAndPath(DragonoidExpanded.MODID,
                                                        "textures/entity/frilled_drake_drakeling_four_eyes_closed.png"),
                                        ResourceLocation.fromNamespaceAndPath(DragonoidExpanded.MODID,
                                                        "textures/entity/frilled_drake_teen_four_eyes_closed.png"),
                                        ResourceLocation.fromNamespaceAndPath(DragonoidExpanded.MODID,
                                                        "textures/entity/frilled_drake_adult_four_eyes_closed.png"),
                                        ResourceLocation.fromNamespaceAndPath(DragonoidExpanded.MODID,
                                                        "textures/entity/frilled_drake_elder_four_eyes_closed.png"),
                        }
        };
        // Animations must be stored in assets/<modid>/animations with subfolders
        // supported inside the animations folder
        private static final ResourceLocation animation = ResourceLocation.fromNamespaceAndPath(DragonoidExpanded.MODID,
                        "animations/frilled_drake.animation.json");

        @Override
        public ResourceLocation getModelResource(FrilledDrake object) {
                return FrilledDrakeModel.model;
        }

        @Override
        public ResourceLocation getTextureResource(FrilledDrake object) {
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

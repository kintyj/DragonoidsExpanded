package com.kintyj.dragonoidsexpanded.client.renderer.item.model;

import org.jetbrains.annotations.Nullable;

import com.kintyj.dragonoidsexpanded.DragonoidsExpanded;
import com.kintyj.dragonoidsexpanded.item.ManticorePaw;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

public class ManticorePawModel extends GeoModel<ManticorePaw> {
    // Models must be stored in assets/<modid>/geo with subfolders supported inside
	// the geo folder
	private static final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(DragonoidsExpanded.MODID,
			"geo/item/manticore_paw.geo.json");
	// Textures must be stored in assets/<modid>/geo with subfolders supported
	// inside the textures folder
	private static final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(DragonoidsExpanded.MODID,
			"textures/item/manticore_paw.png");
	// Animations must be stored in assets/<modid>/animations with subfolders
	// supported inside the animations folder
	private static final ResourceLocation animation = ResourceLocation.fromNamespaceAndPath(
			DragonoidsExpanded.MODID,
			"animations/item/manticore_paw.animation.json");

    @Override
    public ResourceLocation getAnimationResource(ManticorePaw animatable) {
        return ManticorePawModel.animation;
    }

    @Override
    public ResourceLocation getModelResource(ManticorePaw animatable, @Nullable GeoRenderer<ManticorePaw> renderer) {
        return ManticorePawModel.model;
    }

    @Override
    public ResourceLocation getTextureResource(ManticorePaw animatable, @Nullable GeoRenderer<ManticorePaw> renderer) {
        return ManticorePawModel.texture;
    }
    
}

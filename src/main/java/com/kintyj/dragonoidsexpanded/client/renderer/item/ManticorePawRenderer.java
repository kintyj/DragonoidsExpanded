package com.kintyj.dragonoidsexpanded.client.renderer.item;

import com.kintyj.dragonoidsexpanded.client.renderer.item.model.ManticorePawModel;
import com.kintyj.dragonoidsexpanded.item.ManticorePaw;

import software.bernie.geckolib.renderer.GeoItemRenderer;

public class ManticorePawRenderer extends GeoItemRenderer<ManticorePaw> {
	public ManticorePawRenderer() {
		super(new ManticorePawModel());
	}
}

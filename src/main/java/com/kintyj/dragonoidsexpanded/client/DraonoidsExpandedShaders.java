package com.kintyj.dragonoidsexpanded.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;

public class DraonoidsExpandedShaders {
    public static ShaderInstance TINT_SHADER;

    public static void loadShaders() {
        Minecraft mc = Minecraft.getInstance();
        try {
            TINT_SHADER = mc.gameRenderer
                    .loadEffect(ResourceLocation.fromNamespaceAndPath("modid", "shaders/tint_red"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

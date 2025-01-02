package com.kintyj.dragonoidexpanded.client;

import net.minecraft.client.event.EntityRenderersEvent;
import com.kintyj.dragonoidexpanded.DragonoidExpanded;
import com.kintyj.dragonoidexpanded.entity.ModEntities;
import com.kintyj.dragonoidexpanded.client.renderer.entity.FrilledDrakeRenderer;

@Mod.EventBusSubscriber(modid = DragonoidExpanded.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientModEventSubscriber {

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.FRILLED_DRAKE.get(), FrilledDrakeRenderer::new);
    }
}

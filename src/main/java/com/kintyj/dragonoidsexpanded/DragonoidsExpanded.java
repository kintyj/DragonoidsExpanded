package com.kintyj.dragonoidsexpanded;

import org.slf4j.Logger;

import com.kintyj.dragonoidsexpanded.client.renderer.debug.PathfindingDebugRenderer;
import com.kintyj.dragonoidsexpanded.client.renderer.entity.FrilledDrakeRenderer;
import com.kintyj.dragonoidsexpanded.entity.FrilledDrake;
import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(DragonoidsExpanded.MODID)
public class DragonoidsExpanded {
        // Define mod id in a common place for everything to reference
        public static final String MODID = "dragonoidsexpanded";
        // Directly reference a slf4j logger
        public static final Logger LOGGER = LogUtils.getLogger();

        // #region Registers
        public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
        public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
        public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT,
                        MODID);
        public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(
                        Registries.ENTITY_TYPE,
                        MODID);
        public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister
                        .create(Registries.CREATIVE_MODE_TAB, MODID);
        // #endregion

        // #region Sounds
        public static final DeferredHolder<SoundEvent, SoundEvent> FRILLED_DRAKE_YAWN = SOUND_EVENTS
                        .register("entity.frilled_drake.yawn", () -> SoundEvent.createVariableRangeEvent(
                                        ResourceLocation.fromNamespaceAndPath(MODID, "entity.frilled_drake.yawn")));
        public static final DeferredHolder<SoundEvent, SoundEvent> FRILLED_DRAKE_ROAR = SOUND_EVENTS
                        .register("entity.frilled_drake.roar", () -> SoundEvent.createVariableRangeEvent(
                                        ResourceLocation.fromNamespaceAndPath(MODID, "entity.frilled_drake.roar")));
        public static final DeferredHolder<SoundEvent, SoundEvent> FRILLED_DRAKE_ALERT = SOUND_EVENTS
                        .register("entity.frilled_drake.alert", () -> SoundEvent.createVariableRangeEvent(
                                        ResourceLocation.fromNamespaceAndPath(MODID, "entity.frilled_drake.alert")));
        // #endregion

        // #region Entities
        public static final DeferredHolder<EntityType<?>, EntityType<FrilledDrake>> FRILLED_DRAKE = ENTITY_TYPES
                        .register("frilled_drake", () -> EntityType.Builder.of(FrilledDrake::new, MobCategory.MONSTER)
                                        .sized(1.5F, 1.3F).clientTrackingRange(10).build("frilled_drake"));
        public static final DeferredItem<SpawnEggItem> FRILLED_DRAKE_ADULT_SPAWN_EGG = ITEMS.register(
                        "frilled_drake_adult_spawn_egg",
                        () -> new DeferredSpawnEggItem(FRILLED_DRAKE, 0xDFDFDF, 0x99CFE8, new Item.Properties()));
        // #endregion

        // Creates a new Block with the id "examplemod:example_block", combining the
        // namespace and path
        public static final DeferredBlock<Block> EXAMPLE_BLOCK = BLOCKS.registerSimpleBlock("example_block",
                        BlockBehaviour.Properties.of().mapColor(MapColor.STONE));
        // Creates a new BlockItem with the id "examplemod:example_block", combining the
        // namespace and path
        public static final DeferredItem<BlockItem> EXAMPLE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("example_block",
                        EXAMPLE_BLOCK);

        // Creates a new food item with the id "examplemod:example_id", nutrition 1 and
        // saturation 2
        public static final DeferredItem<Item> EXAMPLE_ITEM = ITEMS.registerSimpleItem("example_item",
                        new Item.Properties().food(new FoodProperties.Builder()
                                        .alwaysEdible().nutrition(1).saturationModifier(2f).build()));

        // Creates a creative tab with the id "examplemod:example_tab" for the example
        // item, that is placed after the combat tab
        public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS
                        .register("dragonoid_expanded_items_tab", () -> CreativeModeTab.builder()
                                        .title(Component.translatable("itemGroup.dragonoidexpanded")) // The language
                                                                                                      // key for the
                                                                                                      // title of
                                                                                                      // your
                                                                                                      // CreativeModeTab
                                        .withTabsBefore(CreativeModeTabs.COMBAT)
                                        .icon(() -> EXAMPLE_ITEM.get().getDefaultInstance())
                                        .displayItems((parameters, output) -> {
                                                output.accept(EXAMPLE_ITEM.get()); // Add the example item to the tab.
                                                                                   // For your own tabs, this
                                                                                   // method is preferred over the event
                                        }).build());

        // The constructor for the mod class is the first code that is run when your mod
        // is loaded.
        // FML will recognize some parameter types like IEventBus or ModContainer and
        // pass them in automatically.
        public DragonoidsExpanded(IEventBus modEventBus, ModContainer modContainer, Dist dist) {
                // Register the commonSetup method for modloading
                modEventBus.addListener(this::commonSetup);

                // #region Subscribe registers to event bus
                BLOCKS.register(modEventBus);
                ITEMS.register(modEventBus);
                ENTITY_TYPES.register(modEventBus);
                CREATIVE_MODE_TABS.register(modEventBus);
                SOUND_EVENTS.register(modEventBus);
                // #endregion

                // Register ourselves for server and other game events we are interested in.
                // Note that this is necessary if and only if we want *this* class (ExampleMod)
                // to respond directly to events.
                // Do not add this line if there are no @SubscribeEvent-annotated functions in
                // this class, like onServerStarting() below.
                NeoForge.EVENT_BUS.register(this);

                // Register the item to a creative tab
                modEventBus.addListener(this::addCreative);
                modEventBus.addListener(this::registerEntityAttributes);

                // Register our mod's ModConfigSpec so that FML can create and load the config
                // file for us
                modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

                if (dist == Dist.CLIENT) {
                        modEventBus.addListener(this::registerEntityRenderers);
                }
        }

        private void commonSetup(final FMLCommonSetupEvent event) {

        }

        // Add the example block item to the building blocks tab
        private void addCreative(BuildCreativeModeTabContentsEvent event) {
                if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS)
                        event.accept(EXAMPLE_BLOCK_ITEM);
                else if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
                        event.accept(FRILLED_DRAKE_ADULT_SPAWN_EGG);
                }
        }

        public void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
                event.registerEntityRenderer(FRILLED_DRAKE.get(), FrilledDrakeRenderer::new);
        }

        public void registerEntityAttributes(EntityAttributeCreationEvent event) {
                event.put(FRILLED_DRAKE.get(), FrilledDrake.createMobAttributes().build());
        }

        // You can use SubscribeEvent and let the Event Bus discover methods to call
        @SubscribeEvent
        public void onServerStarting(ServerStartingEvent event) {
                // Do something when the server starts
                LOGGER.info("HELLO from server starting");
        }

        // You can use EventBusSubscriber to automatically register all static methods
        // in the class annotated with @SubscribeEvent
        @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
        public static class ClientModEvents {
                @SubscribeEvent
                public static void onClientSetup(FMLClientSetupEvent event) {
                        // Some client setup code
                        LOGGER.info("HELLO FROM CLIENT SETUP");
                        LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
                }

                /*
                 * public static void onRenderLevel(RenderLevelStageEvent event) {
                 * Minecraft mc = Minecraft.getInstance();
                 * if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS &&
                 * true && mc.player != null
                 * && mc.level != null) {
                 * mc.level.entitiesForRendering().forEach(entity -> {
                 * PathfindingDebugRenderer.render(entity, event.getPoseStack(),
                 * mc.renderBuffers().bufferSource(),
                 * event.getPartialTick().getGameTimeDeltaTicks());
                 * });
                 * }
                 * }
                 */
        }
}

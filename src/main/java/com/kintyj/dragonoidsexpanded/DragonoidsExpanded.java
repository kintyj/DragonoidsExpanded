package com.kintyj.dragonoidsexpanded;

import org.slf4j.Logger;

import com.kintyj.dragonoidsexpanded.block.SlimyBlock;
import com.kintyj.dragonoidsexpanded.client.renderer.entity.FrilledDrakeRenderer;
import com.kintyj.dragonoidsexpanded.client.renderer.entity.ManticoreRenderer;
import com.kintyj.dragonoidsexpanded.client.renderer.entity.WyvernRenderer;
import com.kintyj.dragonoidsexpanded.effect.Mortis;
import com.kintyj.dragonoidsexpanded.entity.FrilledDrake;
import com.kintyj.dragonoidsexpanded.entity.Manticore;
import com.kintyj.dragonoidsexpanded.entity.Wyvern;
import com.kintyj.dragonoidsexpanded.item.DrakelordsMace;
import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
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
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
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

	// #region Doot A Packs
	public static final ResourceKey<Registry<Wyvern.WyvernColor>> WYVERN_COLOR_REGISTRY_KEY = ResourceKey
			.createRegistryKey(ResourceLocation.fromNamespaceAndPath(MODID, "wyvern_colors"));
	public static final ResourceKey<Registry<Wyvern.WyvernType>> WYVERN_TYPE_REGISTRY_KEY = ResourceKey
			.createRegistryKey(ResourceLocation.fromNamespaceAndPath(MODID, "wyvern_types"));
	// #endregion

	// #region Registers
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
	public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, MODID);
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
	public static final DeferredHolder<SoundEvent, SoundEvent> WYVERN_CALL = SOUND_EVENTS
			.register("entity.wyvern.call", () -> SoundEvent.createVariableRangeEvent(
					ResourceLocation.fromNamespaceAndPath(MODID, "entity.wyvern.call")));
	public static final DeferredHolder<SoundEvent, SoundEvent> MANTICORE_ROAR = SOUND_EVENTS
			.register("entity.manticore.m_roar", () -> SoundEvent.createVariableRangeEvent(
					ResourceLocation.fromNamespaceAndPath(MODID, "entity.manticore.m_roar")));
	// #endregion

	// #region Effects
	public static final DeferredHolder<MobEffect, MobEffect> MORTIS = EFFECTS
			.register("mortis", Mortis::new);
	// #endregion

	// #region Entities
	public static final DeferredHolder<EntityType<?>, EntityType<FrilledDrake>> FRILLED_DRAKE = ENTITY_TYPES
			.register("frilled_drake", () -> EntityType.Builder.of(FrilledDrake::new, MobCategory.MONSTER)
					.sized(1.5F, 1.3F).clientTrackingRange(10).build("frilled_drake"));
	public static final DeferredItem<SpawnEggItem> FRILLED_DRAKE_SPAWN_EGG = ITEMS.register(
			"frilled_drake_spawn_egg",
			() -> new DeferredSpawnEggItem(FRILLED_DRAKE, 0xDFDFDF, 0x99CFE8, new Item.Properties()));

	public static final DeferredHolder<EntityType<?>, EntityType<Manticore>> MANTICORE = ENTITY_TYPES
			.register("manticore", () -> EntityType.Builder.of(Manticore::new, MobCategory.MONSTER)
					.sized(1.5F, 1.8F).clientTrackingRange(10).build("manticore"));
	public static final DeferredItem<SpawnEggItem> MANTICORE_SPAWN_EGG = ITEMS.register(
			"manticore_spawn_egg",
			() -> new DeferredSpawnEggItem(MANTICORE, 0xDFDFDF, 0x99CFE8, new Item.Properties()));

	public static final DeferredHolder<EntityType<?>, EntityType<Wyvern>> WYVERN = ENTITY_TYPES
			.register("wyvern", () -> EntityType.Builder.of(Wyvern::new, MobCategory.MONSTER)
					.sized(1.5F, 1.8F).clientTrackingRange(10).build("wyvern"));
	public static final DeferredItem<SpawnEggItem> WYVERN_SPAWN_EGG = ITEMS.register(
			"wyvern_spawn_egg",
			() -> new DeferredSpawnEggItem(WYVERN, 0xDFDFDF, 0x99CFE8, new Item.Properties()));
	// #endregion

	// #region Blocks
	public static final DeferredBlock<Block> EXAMPLE_BLOCK = BLOCKS.registerSimpleBlock("example_block",
			BlockBehaviour.Properties.of().mapColor(MapColor.STONE));
	public static final DeferredItem<BlockItem> EXAMPLE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("example_block",
			EXAMPLE_BLOCK);
	public static final DeferredBlock<Block> SLIMY_COBBLESTONE = BLOCKS.registerBlock("slimy_cobblestone",
			SlimyBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).friction(0.9F));
	public static final DeferredItem<BlockItem> SLIMY_COBBLESTONE_ITEM = ITEMS.registerSimpleBlockItem(
			"slimy_cobblestone",
			SLIMY_COBBLESTONE);
	public static final DeferredBlock<Block> SLIMY_MOSSY_COBBLESTONE = BLOCKS.registerBlock(
			"slimy_mossy_cobblestone", SlimyBlock::new,
			BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).friction(0.9F));
	public static final DeferredItem<BlockItem> SLIMY_MOSSY_COBBLESTONE_ITEM = ITEMS.registerSimpleBlockItem(
			"slimy_mossy_cobblestone",
			SLIMY_MOSSY_COBBLESTONE);
	public static final DeferredBlock<Block> SLIMY_STONE = BLOCKS.registerBlock("slimy_stone", SlimyBlock::new,
			BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).friction(0.9F));
	public static final DeferredItem<BlockItem> SLIMY_STONE_ITEM = ITEMS.registerSimpleBlockItem("slimy_stone",
			SLIMY_STONE);
	public static final DeferredBlock<Block> SLIMY_SMOOTH_STONE = BLOCKS.registerBlock("slimy_smooth_stone",
			SlimyBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).friction(0.9F));
	public static final DeferredItem<BlockItem> SLIMY_SMOOTH_STONE_ITEM = ITEMS.registerSimpleBlockItem(
			"slimy_smooth_stone",
			SLIMY_SMOOTH_STONE);
	public static final DeferredBlock<Block> SLIMY_GRANITE = BLOCKS.registerBlock("slimy_granite",
			SlimyBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).friction(0.9F));
	public static final DeferredItem<BlockItem> SLIMY_GRANITE_ITEM = ITEMS.registerSimpleBlockItem("slimy_granite",
			SLIMY_GRANITE);
	public static final DeferredBlock<Block> SLIMY_POLISHED_GRANITE = BLOCKS.registerBlock(
			"slimy_polished_granite",
			SlimyBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).friction(0.9F));
	public static final DeferredItem<BlockItem> SLIMY_POLISHED_GRANITE_ITEM = ITEMS.registerSimpleBlockItem(
			"slimy_polished_granite",
			SLIMY_POLISHED_GRANITE);
	public static final DeferredBlock<Block> SLIMY_DIORITE = BLOCKS.registerBlock("slimy_diorite",
			SlimyBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).friction(0.9F));
	public static final DeferredItem<BlockItem> SLIMY_DIORITE_ITEM = ITEMS.registerSimpleBlockItem("slimy_diorite",
			SLIMY_DIORITE);
	public static final DeferredBlock<Block> SLIMY_POLISHED_DIORITE = BLOCKS.registerBlock(
			"slimy_polished_diorite",
			SlimyBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).friction(0.9F));
	public static final DeferredItem<BlockItem> SLIMY_POLISHED_DIORITE_ITEM = ITEMS.registerSimpleBlockItem(
			"slimy_polished_diorite",
			SLIMY_POLISHED_DIORITE);
	public static final DeferredBlock<Block> SLIMY_ANDESITE = BLOCKS.registerBlock("slimy_andesite",
			SlimyBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).friction(0.9F));
	public static final DeferredItem<BlockItem> SLIMY_ANDESITE_ITEM = ITEMS.registerSimpleBlockItem("slimy_andesite",
			SLIMY_ANDESITE);
	public static final DeferredBlock<Block> SLIMY_POLISHED_ANDESITE = BLOCKS.registerBlock(
			"slimy_polished_andesite",
			SlimyBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).friction(0.9F));
	public static final DeferredItem<BlockItem> SLIMY_POLISHED_ANDESITE_ITEM = ITEMS.registerSimpleBlockItem(
			"slimy_polished_andesite",
			SLIMY_POLISHED_ANDESITE);
	public static final DeferredBlock<Block> SLIMY_STONE_BRICKS = BLOCKS.registerBlock("slimy_stone_bricks",
			SlimyBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).friction(0.9F));
	public static final DeferredItem<BlockItem> SLIMY_STONE_BRICKS_ITEM = ITEMS.registerSimpleBlockItem(
			"slimy_stone_bricks",
			SLIMY_STONE_BRICKS);
	public static final DeferredBlock<Block> SLIMY_MOSSY_STONE_BRICKS = BLOCKS.registerBlock(
			"slimy_mossy_stone_bricks",
			SlimyBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).friction(0.9F));
	public static final DeferredItem<BlockItem> SLIMY_MOSSY_STONE_BRICKS_ITEM = ITEMS.registerSimpleBlockItem(
			"slimy_mossy_stone_bricks",
			SLIMY_MOSSY_STONE_BRICKS);
	public static final DeferredBlock<Block> SLIMY_CRACKED_STONE_BRICKS = BLOCKS.registerBlock(
			"slimy_cracked_stone_bricks",
			SlimyBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).friction(0.9F));
	public static final DeferredItem<BlockItem> SLIMY_CRACKED_STONE_BRICKS_ITEM = ITEMS.registerSimpleBlockItem(
			"slimy_cracked_stone_bricks",
			SLIMY_CRACKED_STONE_BRICKS);
	public static final DeferredBlock<Block> SLIMY_CHISELED_STONE_BRICKS = BLOCKS.registerBlock(
			"slimy_chiseled_stone_bricks",
			SlimyBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).friction(0.9F));
	public static final DeferredItem<BlockItem> SLIMY_CHISELED_STONE_BRICKS_ITEM = ITEMS.registerSimpleBlockItem(
			"slimy_chiseled_stone_bricks",
			SLIMY_CHISELED_STONE_BRICKS);
	// #endregion

	// #region Items
	public static final DeferredItem<Item> EXAMPLE_ITEM = ITEMS.registerSimpleItem("example_item",
			new Item.Properties().food(new FoodProperties.Builder()
					.alwaysEdible().nutrition(1).saturationModifier(2f).build()));
	public static final DeferredItem<Item> DRAKE_HEART_SCALE = ITEMS.registerSimpleItem("drake_heart_scale",
			new Item.Properties());
	public static final DeferredItem<Item> DRAKE_MEAL = ITEMS.registerSimpleItem("drake_meal",
			new Item.Properties());
	public static final DeferredItem<Item> FRILLED_DRAKE_SCALE_BLUE = ITEMS.registerSimpleItem(
			"frilled_drake_scale_blue",
			new Item.Properties());
	public static final DeferredItem<Item> FRILLED_DRAKE_SCALE_GREEN = ITEMS.registerSimpleItem(
			"frilled_drake_scale_green",
			new Item.Properties());
	public static final DeferredItem<Item> FRILLED_DRAKE_SCALE_AQUA = ITEMS.registerSimpleItem(
			"frilled_drake_scale_aqua",
			new Item.Properties());
	public static final DeferredItem<Item> FRILLED_DRAKE_SCALE_TURQUOISE = ITEMS.registerSimpleItem(
			"frilled_drake_scale_turquoise",
			new Item.Properties());
	public static final DeferredItem<Item> FRILLED_DRAKE_EGG_BLUE = ITEMS.registerSimpleItem(
			"frilled_drake_egg_blue",
			new Item.Properties());
	public static final DeferredItem<Item> FRILLED_DRAKE_EGG_AQUA = ITEMS.registerSimpleItem(
			"frilled_drake_egg_aqua",
			new Item.Properties());
	public static final DeferredItem<Item> FRILLED_DRAKE_EGG_TURQUOISE = ITEMS.registerSimpleItem(
			"frilled_drake_egg_turquoise",
			new Item.Properties());
	public static final DeferredItem<Item> FRILLED_DRAKE_EGG_GREEN = ITEMS.registerSimpleItem(
			"frilled_drake_egg_green",
			new Item.Properties());
	public static final DeferredItem<Item> DRAKE_LORDS_MACE = ITEMS.registerItem(
			"drake_lords_mace", DrakelordsMace::new,
			new Item.Properties().attributes(DrakelordsMace.createAttributes()));
	// #endregion
	// Creates a creative tab with the id "examplemod:example_tab" for the example
	// item, that is placed after the combat tab
	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> DRAGONOIDS_EXPANDED_ITEMS_TAB = CREATIVE_MODE_TABS
			.register("dragonoids_expanded_items_tab", () -> CreativeModeTab.builder()
					.title(Component.translatable("itemGroup.dragonoidsexpanded")) // The language
																					// key for the
																					// title of
																					// your
																					// CreativeModeTab
					.withTabsBefore(CreativeModeTabs.COMBAT)
					.icon(() -> EXAMPLE_ITEM.get().getDefaultInstance())
					.displayItems((parameters, output) -> {
						output.accept(EXAMPLE_ITEM.get());
						output.accept(DRAKE_MEAL.get());
						output.accept(DRAKE_HEART_SCALE.get());
						output.accept(FRILLED_DRAKE_SCALE_BLUE.get());
						output.accept(FRILLED_DRAKE_SCALE_GREEN.get());
						output.accept(FRILLED_DRAKE_SCALE_AQUA.get());
						output.accept(FRILLED_DRAKE_SCALE_TURQUOISE.get());
						output.accept(FRILLED_DRAKE_EGG_BLUE.get());
						output.accept(FRILLED_DRAKE_EGG_AQUA.get());
						output.accept(FRILLED_DRAKE_EGG_TURQUOISE.get());
						output.accept(FRILLED_DRAKE_EGG_GREEN.get());
						output.accept(DRAKE_LORDS_MACE.get());
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
		EFFECTS.register(modEventBus);
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
		if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
			event.accept(EXAMPLE_BLOCK_ITEM);
			event.accept(SLIMY_STONE_ITEM);
			event.accept(SLIMY_SMOOTH_STONE_ITEM);
			event.accept(SLIMY_GRANITE_ITEM);
			event.accept(SLIMY_POLISHED_GRANITE_ITEM);
			event.accept(SLIMY_DIORITE_ITEM);
			event.accept(SLIMY_POLISHED_DIORITE_ITEM);
			event.accept(SLIMY_ANDESITE_ITEM);
			event.accept(SLIMY_POLISHED_ANDESITE_ITEM);
			event.accept(SLIMY_STONE_BRICKS_ITEM);
			event.accept(SLIMY_MOSSY_STONE_BRICKS_ITEM);
			event.accept(SLIMY_CRACKED_STONE_BRICKS_ITEM);
			event.accept(SLIMY_CHISELED_STONE_BRICKS_ITEM);
			event.accept(SLIMY_COBBLESTONE_ITEM);
			event.accept(SLIMY_MOSSY_COBBLESTONE_ITEM);
		} else if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
			event.accept(FRILLED_DRAKE_SPAWN_EGG);
			event.accept(MANTICORE_SPAWN_EGG);
			event.accept(WYVERN_SPAWN_EGG);
		}
	}

	public void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(FRILLED_DRAKE.get(), FrilledDrakeRenderer::new);
		event.registerEntityRenderer(MANTICORE.get(), ManticoreRenderer::new);
		event.registerEntityRenderer(WYVERN.get(), WyvernRenderer::new);
	}

	public void registerEntityAttributes(EntityAttributeCreationEvent event) {
		event.put(FRILLED_DRAKE.get(), FrilledDrake.createMobAttributes().build()); // Launch.json is eternal, Boom
																					// eternal.
		event.put(MANTICORE.get(), Manticore.createMobAttributes().build()); // LIKE THAT WILL EVER HAPPEN (Shrek clip
																				// here)
		event.put(WYVERN.get(), Wyvern.createMobAttributes().build());
	}

	/* 
	@SubscribeEvent
	public static void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
		// event.dataPackRegistry(WYVERN_TYPE_REGISTRY_KEY, Wyvern.WyvernType.CODEC,
		// Wyvern.WyvernType.CODEC);
	}*/
 
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

package com.kintyj.dragonoidsexpanded.world.structure;

import java.util.Optional;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.kintyj.dragonoidsexpanded.DragonoidsExpanded;
import com.kintyj.dragonoidsexpanded.entity.Wyvern;
import com.kintyj.dragonoidsexpanded.entity.wyvern.WyvernType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.neoforged.neoforge.common.world.ModifiableStructureInfo;

public class WyvernNest extends Structure {

    // A custom codec that changes the size limit for our
    // code_structure_sky_fan.json's config to not be capped at 7.
    // With this, we can have a structure with a size limit up to 30 if we want to
    // have extremely long branches of pieces in the structure.
    public static final MapCodec<WyvernNest> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            WyvernNest.settingsCodec(instance),
            StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
            ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name")
                    .forGetter(structure -> structure.startJigsawName),
            Codec.intRange(0, 30).fieldOf("size").forGetter(structure -> structure.size),
            HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
            Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap")
                    .forGetter(structure -> structure.projectStartToHeightmap),
            Codec.intRange(1, 128).fieldOf("max_distance_from_center")
                    .forGetter(structure -> structure.maxDistanceFromCenter),
            DimensionPadding.CODEC.optionalFieldOf("dimension_padding", JigsawStructure.DEFAULT_DIMENSION_PADDING)
                    .forGetter(structure -> structure.dimensionPadding),
            LiquidSettings.CODEC.optionalFieldOf("liquid_settings", JigsawStructure.DEFAULT_LIQUID_SETTINGS)
                    .forGetter(structure -> structure.liquidSettings))
            .apply(instance, WyvernNest::new));

    private final Holder<StructureTemplatePool> startPool;
    private final Optional<ResourceLocation> startJigsawName;
    private final int size;
    private final HeightProvider startHeight;
    private final Optional<Heightmap.Types> projectStartToHeightmap;
    private final int maxDistanceFromCenter;
    private final DimensionPadding dimensionPadding;
    private final LiquidSettings liquidSettings;

    public WyvernNest(Structure.StructureSettings config,
            Holder<StructureTemplatePool> startPool,
            Optional<ResourceLocation> startJigsawName,
            int size,
            HeightProvider startHeight,
            Optional<Heightmap.Types> projectStartToHeightmap,
            int maxDistanceFromCenter,
            DimensionPadding dimensionPadding,
            LiquidSettings liquidSettings) {
        super(config);
        this.startPool = startPool;
        this.startJigsawName = startJigsawName;
        this.size = size;
        this.startHeight = startHeight;
        this.projectStartToHeightmap = projectStartToHeightmap;
        this.maxDistanceFromCenter = maxDistanceFromCenter;
        this.dimensionPadding = dimensionPadding;
        this.liquidSettings = liquidSettings;
    }

    @Override
    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext context) {
        // Set's our spawning blockpos's y offset to be 60 blocks up.
        // Since we are going to have heightmap/terrain height spawning set to true
        // further down, this will make it so we spawn 60 blocks above terrain.
        // If we wanted to spawn on ocean floor, we would set heightmap/terrain height
        // spawning to false and the grab the y value of the terrain with OCEAN_FLOOR_WG
        // heightmap.
        int startY = this.startHeight.sample(context.random(),
                new WorldGenerationContext(context.chunkGenerator(), context.heightAccessor()));

        // Turns the chunk coordinates into actual coordinates we can use. (Gets corner
        // of that chunk)
        ChunkPos chunkPos = context.chunkPos();
        BlockPos blockPos = new BlockPos(chunkPos.getMinBlockX(), startY, chunkPos.getMinBlockZ());

        Optional<Structure.GenerationStub> structurePiecesGenerator = JigsawPlacement.addPieces(
                context, // Used for JigsawPlacement to get all the proper behaviors done.
                this.startPool, // The starting pool to use to create the structure layout from
                this.startJigsawName, // Can be used to only spawn from one Jigsaw block. But we don't need to worry
                                      // about this.
                this.size, // How deep a branch of pieces can go away from center piece. (5 means branches
                           // cannot be longer than 5 pieces from center piece)
                blockPos, // Where to spawn the structure.
                false, // "useExpansionHack" This is for legacy villages to generate properly. You
                       // should keep this false always.
                this.projectStartToHeightmap, // Adds the terrain height's y value to the passed in blockpos's y value.
                                              // (This uses WORLD_SURFACE_WG heightmap which stops at top water too)
                // Here, blockpos's y value is 60 which means the structure spawn 60 blocks
                // above terrain height.
                // Set this to false for structure to be place only at the passed in blockpos's
                // Y value instead.
                // Definitely keep this false when placing structures in the nether as
                // otherwise, heightmap placing will put the structure on the Bedrock roof.
                this.maxDistanceFromCenter, // Maximum limit for how far pieces can spawn from center. You cannot set
                                            // this bigger than 128 or else pieces gets cutoff.
                PoolAliasLookup.EMPTY, // Optional thing that allows swapping a template pool with another per
                                       // structure json instance. We don't need this but see vanilla JigsawStructure
                                       // class for how to wire it up if you want it.
                this.dimensionPadding, // Optional thing to prevent generating too close to the bottom or top of the
                                       // dimension.
                this.liquidSettings); // Optional thing to control whether the structure will be waterlogged when
                                      // replacing pre-existing water in the world.

        // Return the pieces generator that is now set up so that the game runs it when
        // it needs to create the layout of structure pieces.
        return structurePiecesGenerator;
    }

    @Override
    public StructureType<?> type() {
        return DragonoidsExpanded.WYVERN_NEST.get(); // Helps the game know how to turn this structure back to json to
                                                     // save
                                                     // to chunks
    }

    @Override
    public void afterPlace(WorldGenLevel level, StructureManager structureManager, ChunkGenerator chunkGenerator,
            RandomSource random, BoundingBox boundingBox, ChunkPos chunkPos, PiecesContainer pieces) {
        Wyvern wyvern = DragonoidsExpanded.WYVERN.get().create(level.getLevel(), (wyver) -> {
        }, boundingBox.getCenter(), MobSpawnType.CHUNK_GENERATION, false, false);

        Registry<WyvernType> reg = level.getLevel().registryAccess()
                .registry(DragonoidsExpanded.WYVERN_TYPE_REGISTRY_KEY).get();

        DragonoidsExpanded.LOGGER.info("" + reg.getId(reg.getRandom(random).get().value()));

        wyvern.setWyvernType(reg.getId(reg.getRandom(random).get().value()));

        level.addFreshEntity(wyvern);
    }

}
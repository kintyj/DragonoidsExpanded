package com.kintyj.dragonoidsexpanded.world.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class WyvernNest {
        public static boolean placeStructure(String structureName, ServerLevel world, BlockPos pos) {
        // Load the structure
        StructureTemplate template = world.getStructureManager()
                .get(new ResourceLocation("dragonoidexpanded", WyvernNest))
                .orElse(null);

        if (template == null) {
            System.out.println("Failed to load structure: " + structureName);
            return false;
        }

        // Place the structure
        return template.placeInWorld(
                world, pos, pos,
                new StructurePlaceSettings(), // Placement settings (rotation, mirror, etc.)
                world.random,
                2 // Update flags (2 = update blocks)
        );
    }
}

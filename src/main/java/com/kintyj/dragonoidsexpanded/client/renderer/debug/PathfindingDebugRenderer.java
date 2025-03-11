package com.kintyj.dragonoidsexpanded.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;

public class PathfindingDebugRenderer {
    public static void render(Entity entity, PoseStack poseStack, MultiBufferSource bufferSource, float partialTicks) {
        if (entity instanceof Mob mob) {
            Path path = mob.getNavigation().getPath();
            if (path != null) {
                renderPath(path, entity.level(), poseStack, bufferSource, partialTicks);
            }
        }
    }

    private static void renderPath(Path path, Level level, PoseStack poseStack, MultiBufferSource bufferSource,
            float partialTicks) {
        for (int i = 0; i < path.getNodeCount(); i++) {
            Node node = path.getNode(i);
            renderNode(node, level, poseStack, bufferSource, i == path.getNextNodeIndex());
        }
    }

    private static void renderNode(Node node, Level level, PoseStack poseStack, MultiBufferSource bufferSource,
            boolean isNext) {
        double x = node.x + 0.5;
        double y = node.y + 0.5;
        double z = node.z + 0.5;

        // Render a red cube for the current node
        float red = isNext ? 1.0f : 0.5f;
        float green = isNext ? 0.0f : 0.5f;
        float blue = 0.0f;

        DebugRenderer.renderFilledBox(poseStack, bufferSource, new AABB(x - 0.25, y - 0.25, z - 0.25, x + 0.25, y + 0.25, z + 0.25), red, green, blue, 0.5f);
    }
}

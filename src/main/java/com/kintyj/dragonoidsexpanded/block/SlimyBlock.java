package com.kintyj.dragonoidsexpanded.block;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.kintyj.dragonoidsexpanded.block.state.properties.ModBlockStateProperties;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class SlimyBlock extends Block {
    public static final IntegerProperty SLIME_STATE = ModBlockStateProperties.SLIMY_STATE;

    public SlimyBlock(Properties p_49795_) {
        super(p_49795_);
        registerDefaultState(stateDefinition.any().setValue(SLIME_STATE, 0));
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(SLIME_STATE);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(@Nonnull BlockPlaceContext pContext) {
        return defaultBlockState().setValue(SLIME_STATE, 0);
    }

    @Override
    public void fallOn(@Nonnull Level level, @Nonnull BlockState state, @Nonnull BlockPos pos, @Nonnull Entity entity,
            float fallDistance) {
        if (entity.isSuppressingBounce()) {
            super.fallOn(level, state, pos, entity, fallDistance);
        } else {
            entity.causeFallDamage(fallDistance, 0.0F, level.damageSources().fall());
        }
    }

    @Override
    public void updateEntityMovementAfterFallOn(@Nonnull BlockGetter level, @Nonnull Entity entity) {
        if (entity.isSuppressingBounce()) {
            super.updateEntityMovementAfterFallOn(level, entity);
        } else {
            this.bounceUp(entity);
        }
    }

    private void bounceUp(Entity entity) {
        Vec3 vec3 = entity.getDeltaMovement();
        if (vec3.y < 0.0) {
            double d0 = entity instanceof LivingEntity ? 0.5 : 0.4;
            entity.setDeltaMovement(vec3.x, -vec3.y * d0, vec3.z);
        }
    }

    @Override
    public void stepOn(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull Entity entity) {
        double d0 = Math.abs(entity.getDeltaMovement().y);
        if (d0 < 0.1 && !entity.isSteppingCarefully()) {
            double d1 = 0.4 + d0 * 0.2;
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(d1, 1.0, d1));
        }

        super.stepOn(level, pos, state, entity);
    }

    @Override
    protected InteractionResult useItemOn(@Nonnull ItemStack stack, @Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Player player,
            @Nonnull InteractionHand hand, @Nonnull BlockHitResult hitResult) {
        if (stack.is(Items.GLASS_BOTTLE) && state.getValue(SLIME_STATE) > 0) {
            stack.shrink(1);
            player.addItem(new ItemStack(Items.HONEY_BOTTLE, 1));
            level.setBlock(pos, state.setValue(SLIME_STATE, state.getValue(SLIME_STATE) - 1), UPDATE_ALL);
        } else if (stack.is(Items.SLIME_BALL) && state.getValue(SLIME_STATE) < 3) {
            stack.shrink(1);
            level.setBlock(pos, state.setValue(SLIME_STATE, state.getValue(SLIME_STATE) + 1), UPDATE_ALL);
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }
}

package com.kintyj.dragonoidsexpanded.item;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import com.google.common.collect.Sets;
import com.kintyj.dragonoidsexpanded.DragonoidsExpanded;
import com.kintyj.dragonoidsexpanded.component.WhipStateComponent;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ItemAbility;

public class Whip extends Item {
    private static final float MAX_CHARGE_DURATION = 2.25F;
    /**
     * Set to {@code true} when the crossbow is 20% charged.
     */
    private boolean startSoundPlayed = false;
    /**
     * Set to {@code true} when the crossbow is 50% charged.
     */
    private boolean midLoadSoundPlayed = false;
    public static final float DEFAULT_WHIP_STRENGTH = 4.5f;
    private static final Whip.ChargingSounds DEFAULT_SOUNDS = new Whip.ChargingSounds(
        Optional.of(DragonoidsExpanded.WHIP_START), Optional.of(DragonoidsExpanded.WHIP_MIDDLE), Optional.of(DragonoidsExpanded.WHIP_END)
    );

    public static final ItemAbility WHIP_WHIP_ITEM_ABILITY = ItemAbility.get("whip_whip");

    public static final Set<ItemAbility> DEFAULT_WHIP_ACTIONS = of(WHIP_WHIP_ITEM_ABILITY);

    public Whip(ToolMaterial material, Item.Properties properties, float attackDamage, float attackSpeed) {
        super(material.applyToolProperties(properties, DragonoidsExpanded.WHIP_EFFICIENT_TAG_KEY, attackDamage, attackSpeed).component(DragonoidsExpanded.WHIP_STATE, new WhipStateComponent(0)));
    }

    @Override
    public boolean canAttackBlock(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Player player) {
        return !player.isCreative();
    }

    @Override
    public boolean hurtEnemy(@Nonnull ItemStack stack, @Nonnull LivingEntity target, @Nonnull LivingEntity attacker) {
        return true;
    }

    @Override
    public void postHurtEnemy(@Nonnull ItemStack stack, @Nonnull LivingEntity target, @Nonnull LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
    }

    @Override
    public boolean canPerformAction(@Nonnull ItemStack stack, @Nonnull ItemAbility itemAbility) {
        return DEFAULT_WHIP_ACTIONS.contains(itemAbility);
    }

    private static Set<ItemAbility> of(ItemAbility... actions) {
        return Stream.of(actions).collect(Collectors.toCollection(Sets::newIdentityHashSet));
    }

    @SuppressWarnings("null")
    @Override
    public InteractionResult use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        if (player.getItemInHand(hand).get(DragonoidsExpanded.WHIP_STATE.get()).state() == WhipState.IDLE.state) {
            this.startSoundPlayed = false;
            this.midLoadSoundPlayed = false;
            player.startUsingItem(hand);
            return InteractionResult.CONSUME;
        } else {
            return InteractionResult.FAIL;
        }
    }

    @SuppressWarnings("null")
    @Override
    public boolean releaseUsing(ItemStack stack, Level level, LivingEntity player, int p_40878_) {
        int i = this.getUseDuration(stack, player) - p_40878_;
        float f = getPowerForTime(i, stack, player);
        if (f >= 1.0F && stack.get(DragonoidsExpanded.WHIP_STATE.get()).state() == WhipState.IDLE.state) {
            Whip.ChargingSounds whipChargingSounds = this.getChargingSounds(stack);
            whipChargingSounds.end()
                .ifPresent(
                    p_390305_ -> level.playSound(
                            null,
                            player.getX(),
                            player.getY(),
                            player.getZ(),
                            p_390305_.value(),
                            player.getSoundSource(),
                            1.0F,
                            1.0F / (level.getRandom().nextFloat() * 0.5F + 1.0F) + 0.2F
                        )
                );
            return true;
        } else {
            return false;
        }
    }

    @SuppressWarnings("null")
    @Override
    public InteractionResult interactLivingEntity(@Nonnull ItemStack stack, @Nonnull Player player, @Nonnull LivingEntity interactionTarget,
    @Nonnull InteractionHand usedHand) {
        if (stack.get(DragonoidsExpanded.WHIP_STATE.get()).state() == WhipState.COILED.state) {
            stack.set(DragonoidsExpanded.WHIP_STATE.get(), new WhipStateComponent(WhipState.IDLE.state));
            player.getItemInHand(usedHand).set(DragonoidsExpanded.WHIP_STATE.get(), new WhipStateComponent(WhipState.IDLE.state));
            Vec3 direction = player.position().vectorTo(interactionTarget.position()).normalize().scale(-1);
            interactionTarget.knockback(DEFAULT_WHIP_STRENGTH, direction.x, direction.z);
            if (!player.level().isClientSide())
                interactionTarget.hurtServer((ServerLevel)player.level(), player.damageSources().playerAttack(player), (float)player.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
            player.playSound(DragonoidsExpanded.WHIP_CRACK.get());
            return InteractionResult.SUCCESS;
        } else if (stack.get(DragonoidsExpanded.WHIP_STATE.get()).state() == WhipState.COILED.state) {
            DragonoidsExpanded.LOGGER.info("Didn't decompress.");
        } else {
            DragonoidsExpanded.LOGGER.info("Can't whip.");
        }

        return super.interactLivingEntity(stack, player, interactionTarget, usedHand);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack pStack, @Nonnull TooltipContext pContext, @Nonnull List<Component> pTooltipComponents,
    @Nonnull TooltipFlag pTooltipFlag) {

        pTooltipComponents
                .add(Component.literal("State: " + String.valueOf(pStack.get(DragonoidsExpanded.WHIP_STATE))));
        super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
    }

    @SuppressWarnings("null")
    @Override
    public ItemStack finishUsingItem(@Nonnull ItemStack stack, @Nonnull Level level, @Nonnull LivingEntity livingEntity) {
        if (stack.get(DragonoidsExpanded.WHIP_STATE.get()).state() == WhipState.IDLE.state) {
            stack.set(DragonoidsExpanded.WHIP_STATE.get(), new WhipStateComponent(WhipState.COILED.state));
        } else {
            DragonoidsExpanded.LOGGER.info("Already coiled.");
        }

        return super.finishUsingItem(stack, level, livingEntity);
    }

    public enum WhipState {
        IDLE(0),
        COILED(1);

        private final int state;

        WhipState(int state) {
            this.state = state;
        }

        public int getState() {
            return state;
        }
    }

    /**
     * Called as the item is being used by an entity.
     */
    @Override
    public void onUseTick(@Nonnull Level level, @Nonnull LivingEntity livingEntity, @Nonnull ItemStack stack, int count) {
        if (!level.isClientSide) {
            Whip.ChargingSounds whipChargingSounds = this.getChargingSounds(stack);
            float f = (float)(stack.getUseDuration(livingEntity) - count) / (float)getChargeDuration(stack, livingEntity);
            if (f < 0.2F) {
                this.startSoundPlayed = false;
                this.midLoadSoundPlayed = false;
            }

            if (f >= 0.2F && !this.startSoundPlayed) {
                this.startSoundPlayed = true;
                whipChargingSounds.start()
                    .ifPresent(
                        p_390299_ -> level.playSound(
                                null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), p_390299_.value(), SoundSource.PLAYERS, 0.5F, 1.0F
                            )
                    );
            }

            if (f >= 0.5F && !this.midLoadSoundPlayed) {
                this.midLoadSoundPlayed = true;
                whipChargingSounds.mid()
                    .ifPresent(
                        p_390302_ -> level.playSound(
                                null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), p_390302_.value(), SoundSource.PLAYERS, 0.5F, 1.0F
                            )
                    );
            }
        }
    }

    @Override
    public int getUseDuration(@Nonnull ItemStack item, @Nonnull LivingEntity entity) {
        return getChargeDuration(item, entity) + 3;
    }

    public static int getChargeDuration(ItemStack stack, LivingEntity shooter) {
        float f = EnchantmentHelper.modifyCrossbowChargingTime(stack, shooter, MAX_CHARGE_DURATION);
        return Mth.floor(f * 20.0F);
    }

    @Override
    public ItemUseAnimation getUseAnimation(@Nonnull ItemStack item) {
        return ItemUseAnimation.CROSSBOW;
    }

    Whip.ChargingSounds getChargingSounds(ItemStack stack) {
        return DEFAULT_SOUNDS;
    }

    private static float getPowerForTime(int timeLeft, ItemStack stack, LivingEntity shooter) {
        float f = (float)timeLeft / (float)getChargeDuration(stack, shooter);
        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }

    public static record ChargingSounds(Optional<Holder<SoundEvent>> start, Optional<Holder<SoundEvent>> mid, Optional<Holder<SoundEvent>> end) {
        public static final Codec<Whip.ChargingSounds> CODEC = RecordCodecBuilder.create(
            p_345672_ -> p_345672_.group(
                        SoundEvent.CODEC.optionalFieldOf("start").forGetter(Whip.ChargingSounds::start),
                        SoundEvent.CODEC.optionalFieldOf("mid").forGetter(Whip.ChargingSounds::mid),
                        SoundEvent.CODEC.optionalFieldOf("end").forGetter(Whip.ChargingSounds::end)
                    )
                    .apply(p_345672_, Whip.ChargingSounds::new)
        );
    }
}

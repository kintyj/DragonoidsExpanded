package com.kintyj.dragonoidsexpanded.item;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import com.google.common.collect.Sets;
import com.kintyj.dragonoidsexpanded.DragonoidsExpanded;
import com.kintyj.dragonoidsexpanded.component.WhipStateComponent;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbility;

public class Whip extends TieredItem {
    public static final ItemAbility WHIP_WHIP_ITEM_ABILITY = ItemAbility.get("whip_whip");

    public static final Set<ItemAbility> DEFAULT_WHIP_ACTIONS = of(WHIP_WHIP_ITEM_ABILITY);

    public Whip(Tier tier, Item.Properties properties) {
        super(tier, properties.component(DataComponents.TOOL, createToolProperties()).component(DragonoidsExpanded.WHIP_STATE, new WhipStateComponent(0)));
    }

    /**
     * Neo: Allow modded Swords to set exactly what Tool data component to use for their sword.
     */
    public Whip(Tier p_tier, Item.Properties p_properties, Tool toolComponentData) {
        super(p_tier, p_properties.component(DataComponents.TOOL, toolComponentData).component(DragonoidsExpanded.WHIP_STATE, new WhipStateComponent(0)));
    }

    public static Tool createToolProperties() {
        return new Tool(List.of(Tool.Rule.overrideSpeed(DragonoidsExpanded.WHIP_EFFICIENT_TAG_KEY, 1.5F)), 1.0F, 2);
    }

    public static ItemAttributeModifiers createAttributes(Tier tier, int attackDamage, float attackSpeed) {
        return createAttributes(tier, (float)attackDamage, attackSpeed);
    }

    /**
     * Neo: Method overload to allow giving a float for damage instead of an int.
     */
    public static ItemAttributeModifiers createAttributes(Tier p_330371_, float p_331976_, float p_332104_) {
        return ItemAttributeModifiers.builder()
            .add(
                Attributes.ATTACK_DAMAGE,
                new AttributeModifier(
                    BASE_ATTACK_DAMAGE_ID, (double)((float)p_331976_ + p_330371_.getAttackDamageBonus()), AttributeModifier.Operation.ADD_VALUE
                ),
                EquipmentSlotGroup.MAINHAND
            )
            .add(
                Attributes.ATTACK_SPEED,
                new AttributeModifier(BASE_ATTACK_SPEED_ID, (double)p_332104_, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND
            )
            .build();
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

    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        if (player.getItemInHand(hand).get(DragonoidsExpanded.WHIP_STATE.get()).state() == WhipState.IDLE.state) player.startUsingItem(hand);
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    @SuppressWarnings("null")
    @Override
    public InteractionResult interactLivingEntity(@Nonnull ItemStack stack, @Nonnull Player player, @Nonnull LivingEntity interactionTarget,
    @Nonnull InteractionHand usedHand) {

        if (stack.get(DragonoidsExpanded.WHIP_STATE.get()).state() == WhipState.COILED.state) {
            stack.set(DragonoidsExpanded.WHIP_STATE.get(), new WhipStateComponent(WhipState.IDLE.state));
            player.getItemInHand(usedHand).set(DragonoidsExpanded.WHIP_STATE.get(), new WhipStateComponent(WhipState.IDLE.state));
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
        } else if (stack.get(DragonoidsExpanded.WHIP_STATE.get()).state() == WhipState.DECOMPRESS.state) {
            stack.set(DragonoidsExpanded.WHIP_STATE.get(), new WhipStateComponent(WhipState.IDLE.state));
        } else {
            DragonoidsExpanded.LOGGER.info("Already coiled.");
        }

        return super.finishUsingItem(stack, level, livingEntity);
    }

    public enum WhipState {
        IDLE(0),
        COILED(1),
        DECOMPRESS(2);

        private final int state;

        WhipState(int state) {
            this.state = state;
        }

        public int getState() {
            return state;
        }
    }

    @Override
    public int getUseDuration(@Nonnull ItemStack stack, @Nonnull LivingEntity entity) {
        return 60;
    }
}

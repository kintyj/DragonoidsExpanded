package com.kintyj.dragonoidsexpanded.item;

import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import com.kintyj.dragonoidsexpanded.DragonoidsExpanded;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

@SuppressWarnings("unused")
public class DrakelordsMace extends Item {
    private static final int DEFAULT_ATTACK_DAMAGE = 5;
    private static final float DEFAULT_ATTACK_SPEED = -3.4F;
    public static final float SMASH_ATTACK_FALL_THRESHOLD = 1.5F;
    private static final float SMASH_ATTACK_HEAVY_THRESHOLD = 5.0F;
    public static final float SMASH_ATTACK_KNOCKBACK_RADIUS = 3.5F;
    private static final float SMASH_ATTACK_KNOCKBACK_POWER = 0.7F;

    public DrakelordsMace(Item.Properties pProperties) {
        super(pProperties);
    }

    public static ItemAttributeModifiers createAttributes() {
        return ItemAttributeModifiers.builder()
                .add(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 5.0, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .add(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(BASE_ATTACK_SPEED_ID, -3.4F, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .build();
    }

    public static Tool createToolProperties() {
        return new Tool(List.of(), 1.0F, 2);
    }

    @Override
    public boolean canAttackBlock(@Nonnull BlockState pState, @Nonnull Level pLevel, @Nonnull BlockPos pPos,
            @Nonnull Player pPlayer) {
        return !pPlayer.isCreative();
    }

    @Override
    public int getEnchantmentValue() {
        return 15;
    }

    /**
     * Current implementations of this method in child classes do not use the entry
     * argument beside ev. They just raise the damage on the stack.
     */
    @SuppressWarnings("null")
    @Override
    public boolean hurtEnemy(@Nonnull ItemStack pStack, @Nonnull LivingEntity pTarget,
            @Nonnull LivingEntity pAttacker) {
        if (pAttacker instanceof ServerPlayer serverplayer && canSmashAttack(serverplayer)) {
            ServerLevel serverlevel = (ServerLevel) pAttacker.level();
            if (serverplayer.isIgnoringFallDamageFromCurrentImpulse() && serverplayer.currentImpulseImpactPos != null) {
                if (serverplayer.currentImpulseImpactPos.y > serverplayer.position().y) {
                    serverplayer.currentImpulseImpactPos = serverplayer.position();
                }
            } else {
                serverplayer.currentImpulseImpactPos = serverplayer.position();
            }

            serverplayer.setIgnoreFallDamageFromCurrentImpulse(true);
            serverplayer.setDeltaMovement(serverplayer.getDeltaMovement().with(Direction.Axis.Y, 0.01F));
            serverplayer.connection.send(new ClientboundSetEntityMotionPacket(serverplayer));
            if (pTarget.onGround()) {
                serverplayer.setSpawnExtraParticlesOnFall(true);
                SoundEvent soundevent = serverplayer.fallDistance > 5.0F ? SoundEvents.MACE_SMASH_GROUND_HEAVY
                        : SoundEvents.MACE_SMASH_GROUND;
                serverlevel.playSound(
                        null, serverplayer.getX(), serverplayer.getY(), serverplayer.getZ(), soundevent,
                        serverplayer.getSoundSource(), 1.0F, 1.0F);
            } else {
                serverlevel.playSound(
                        null, serverplayer.getX(), serverplayer.getY(), serverplayer.getZ(), SoundEvents.MACE_SMASH_AIR,
                        serverplayer.getSoundSource(), 1.0F, 1.0F);
            }

            knockback(serverlevel, serverplayer, pTarget);
        }

        return true;
    }

    @Override
    public void postHurtEnemy(@Nonnull ItemStack p_345716_, @Nonnull LivingEntity p_345817_,
            @Nonnull LivingEntity p_346003_) {
        p_345716_.hurtAndBreak(1, p_346003_, EquipmentSlot.MAINHAND);
        if (canSmashAttack(p_346003_)) {
            p_346003_.resetFallDistance();
        }
    }

    /**
     * Return whether this item is repairable in an anvil.
     */
    @Override
    public boolean isValidRepairItem(@Nonnull ItemStack pStack, @Nonnull ItemStack pRepairCandidate) {
        return pRepairCandidate.is(DragonoidsExpanded.DRAKE_HEART_SCALE);
    }

    @Override
    public float getAttackDamageBonus(@Nonnull Entity p_344900_, float p_335575_, @Nonnull DamageSource p_344972_) {
        if (p_344972_.getDirectEntity() instanceof LivingEntity livingentity) {
            if (!canSmashAttack(livingentity)) {
                return 0.0F;
            } else {
                float f1 = livingentity.fallDistance;
                float f2;
                if (f1 <= 3.0F) {
                    f2 = 4.0F * f1;
                } else if (f1 <= 8.0F) {
                    f2 = 12.0F + 2.0F * (f1 - 3.0F);
                } else {
                    f2 = 22.0F + f1 - 8.0F;
                }

                return livingentity.level() instanceof ServerLevel serverlevel
                        ? f2 + EnchantmentHelper.modifyFallBasedDamage(serverlevel, livingentity.getWeaponItem(),
                                p_344900_, p_344972_, 0.0F) * f1
                        : f2;
            }
        } else {
            return 0.0F;
        }
    }

    private static void knockback(Level pLevel, Player pPlayer, Entity pEntity) {
        pLevel.levelEvent(2013, pEntity.getOnPos(), 750);
        pLevel.getEntitiesOfClass(LivingEntity.class, pEntity.getBoundingBox().inflate(3.5),
                knockbackPredicate(pPlayer, pEntity))
                .forEach(p_347296_ -> {
                    Vec3 vec3 = p_347296_.position().subtract(pEntity.position());
                    double d0 = getKnockbackPower(pPlayer, p_347296_, vec3);
                    Vec3 vec31 = vec3.normalize().scale(d0);
                    if (d0 > 0.0) {
                        p_347296_.push(vec31.x, 0.7F, vec31.z);
                        if (p_347296_ instanceof ServerPlayer serverplayer) {
                            serverplayer.connection.send(new ClientboundSetEntityMotionPacket(serverplayer));
                        }
                    }
                });
    }

    private static Predicate<LivingEntity> knockbackPredicate(Player pPlayer, Entity pEntity) {
        return p_344407_ -> {
            boolean flag;
            boolean flag1;
            boolean flag2;
            boolean flag6;
            label62: {
                flag = !p_344407_.isSpectator();
                flag1 = p_344407_ != pPlayer && p_344407_ != pEntity;
                flag2 = !pPlayer.isAlliedTo(p_344407_);
                if (p_344407_ instanceof TamableAnimal tamableanimal && tamableanimal.isTame()
                        && pPlayer.getUUID().equals(tamableanimal.getOwnerUUID())) {
                    flag6 = true;
                    break label62;
                }

                flag6 = false;
            }

            boolean flag3;
            label55: {
                flag3 = !flag6;
                if (p_344407_ instanceof ArmorStand armorstand && armorstand.isMarker()) {
                    flag6 = false;
                    break label55;
                }

                flag6 = true;
            }

            boolean flag4 = flag6;
            boolean flag5 = pEntity.distanceToSqr(p_344407_) <= Math.pow(3.5, 2.0);
            return flag && flag1 && flag2 && flag3 && flag4 && flag5;
        };
    }

    private static double getKnockbackPower(Player pPlayer, LivingEntity pEntity, Vec3 pEntityPos) {
        return (3.5 - pEntityPos.length())
                * 0.7F
                * (double) (pPlayer.fallDistance > 5.0F ? 2 : 1)
                * (1.0 - pEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
    }

    public static boolean canSmashAttack(LivingEntity p_344836_) {
        return p_344836_.fallDistance > 1.5F && !p_344836_.isFallFlying();
    }
}

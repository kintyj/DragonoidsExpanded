package com.kintyj.dragonoidsexpanded.item;

import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
    private static final int DEFAULT_ATTACK_DAMAGE = 9;
    private static final float DEFAULT_ATTACK_SPEED = -3.4F;
    public static final float SMASH_ATTACK_FALL_THRESHOLD = 1.5F;
    private static final float SMASH_ATTACK_HEAVY_THRESHOLD = 5.0F;
    public static final float SMASH_ATTACK_KNOCKBACK_RADIUS = 5.5F;
    private static final float SMASH_ATTACK_KNOCKBACK_POWER = 1.7F;

    public DrakelordsMace(Item.Properties p_333796_) {
        super(p_333796_);
    }

    public static ItemAttributeModifiers createAttributes() {
        return ItemAttributeModifiers.builder()
            .add(
                Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, DEFAULT_ATTACK_DAMAGE, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND
            )
            .add(
                Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, DEFAULT_ATTACK_SPEED, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND
            )
            .build();
    }

    public static Tool createToolProperties() {
        return new Tool(List.of(), 1.0F, 2);
    }

    @Override
    public boolean canAttackBlock(@Nonnull BlockState p_333875_, @Nonnull Level p_333847_, @Nonnull BlockPos p_334073_, @Nonnull Player p_334042_) {
        return !p_334042_.isCreative();
    }

    @Override
    public boolean hurtEnemy(@Nonnull ItemStack p_334046_, @Nonnull LivingEntity p_333712_, @Nonnull LivingEntity p_333812_) {
        if (canSmashAttack(p_333812_)) {
            ServerLevel serverlevel = (ServerLevel)p_333812_.level();
            p_333812_.setDeltaMovement(p_333812_.getDeltaMovement().with(Direction.Axis.Y, 0.01F));
            if (p_333812_ instanceof ServerPlayer serverplayer) {
                serverplayer.currentImpulseImpactPos = this.calculateImpactPosition(serverplayer);
                serverplayer.setIgnoreFallDamageFromCurrentImpulse(true);
                serverplayer.connection.send(new ClientboundSetEntityMotionPacket(serverplayer));
            }

            if (p_333712_.onGround()) {
                if (p_333812_ instanceof ServerPlayer serverplayer1) {
                    serverplayer1.setSpawnExtraParticlesOnFall(true);
                }

                SoundEvent soundevent = p_333812_.fallDistance > SMASH_ATTACK_HEAVY_THRESHOLD ? SoundEvents.MACE_SMASH_GROUND_HEAVY : SoundEvents.MACE_SMASH_GROUND;
                serverlevel.playSound(null, p_333812_.getX(), p_333812_.getY(), p_333812_.getZ(), soundevent, p_333812_.getSoundSource(), 1.0F, 0.8F);
            } else {
                serverlevel.playSound(
                    null, p_333812_.getX(), p_333812_.getY(), p_333812_.getZ(), SoundEvents.MACE_SMASH_AIR, p_333812_.getSoundSource(), 1.0F, 1.2F
                );
            }

            knockback(serverlevel, p_333812_, p_333712_);
        }

        return true;
    }

    @SuppressWarnings("null")
    private Vec3 calculateImpactPosition(ServerPlayer player) {
        return player.isIgnoringFallDamageFromCurrentImpulse()
                && player.currentImpulseImpactPos != null
                && player.currentImpulseImpactPos.y <= player.position().y
            ? player.currentImpulseImpactPos
            : player.position();
    }

    @Override
    public void postHurtEnemy(@Nonnull ItemStack p_345716_, @Nonnull LivingEntity p_345817_, @Nonnull LivingEntity p_346003_) {
        p_345716_.hurtAndBreak(1, p_346003_, EquipmentSlot.MAINHAND);
        if (canSmashAttack(p_346003_)) {
            p_346003_.resetFallDistance();
        }
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
                    ? f2 + EnchantmentHelper.modifyFallBasedDamage(serverlevel, livingentity.getWeaponItem(), p_344900_, p_344972_, 0.0F) * f1
                    : f2;
            }
        } else {
            return 0.0F;
        }
    }

    private static void knockback(Level level, Entity attacker, Entity target) {
        level.levelEvent(2013, target.getOnPos(), 750);
        level.getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(SMASH_ATTACK_KNOCKBACK_RADIUS), knockbackPredicate(attacker, target))
            .forEach(p_347296_ -> {
                Vec3 vec3 = p_347296_.position().subtract(target.position());
                double d0 = getKnockbackPower(attacker, p_347296_, vec3);
                Vec3 vec31 = vec3.normalize().scale(d0);
                if (d0 > 0.0) {
                    p_347296_.push(vec31.x, SMASH_ATTACK_KNOCKBACK_POWER, vec31.z);
                    if (p_347296_ instanceof ServerPlayer serverplayer) {
                        serverplayer.connection.send(new ClientboundSetEntityMotionPacket(serverplayer));
                    }
                }
            });
    }

    private static Predicate<LivingEntity> knockbackPredicate(Entity attacker, Entity target) {
        return p_344407_ -> {
            boolean flag;
            boolean flag1;
            boolean flag2;
            boolean flag6;
            label62: {
                flag = !p_344407_.isSpectator();
                flag1 = p_344407_ != attacker && p_344407_ != target;
                flag2 = !attacker.isAlliedTo(p_344407_);
                if (p_344407_ instanceof TamableAnimal tamableanimal && tamableanimal.isTame() && attacker.getUUID().equals(tamableanimal.getOwnerUUID())) {
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
            boolean flag5 = target.distanceToSqr(p_344407_) <= Math.pow(SMASH_ATTACK_KNOCKBACK_RADIUS, 2.0);
            return flag && flag1 && flag2 && flag3 && flag4 && flag5;
        };
    }

    private static double getKnockbackPower(Entity attacker, LivingEntity entity, Vec3 offset) {
        return (SMASH_ATTACK_KNOCKBACK_RADIUS - offset.length())
            * SMASH_ATTACK_KNOCKBACK_POWER
            * (double)(attacker.fallDistance > SMASH_ATTACK_HEAVY_THRESHOLD ? 2.5 : 1)
            * (1.0 - entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
    }

    public static boolean canSmashAttack(LivingEntity entity) {
        return entity.fallDistance > SMASH_ATTACK_FALL_THRESHOLD && !entity.isFallFlying();
    }

    @Nullable
    @Override
    public DamageSource getDamageSource(@Nonnull LivingEntity p_372868_) {
        return canSmashAttack(p_372868_) ? p_372868_.damageSources().mace(p_372868_) : super.getDamageSource(p_372868_);
    }
}

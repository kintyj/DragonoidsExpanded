package com.kintyj.dragonoidsexpanded.entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.entity.PartEntity;

public class FrilledDrakePart extends PartEntity<FrilledDrake> {
    public final FrilledDrake parentMob;
    public final String name;
    private final EntityDimensions size;

    public FrilledDrakePart(FrilledDrake parentMob, String name, float width, float height) {
        super(parentMob);
        this.size = EntityDimensions.scalable(width, height);
        this.refreshDimensions();
        this.parentMob = parentMob;
        this.name = name;
    }

    @Override
    protected void defineSynchedData(@Nonnull SynchedEntityData.Builder builder) {
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    protected void readAdditionalSaveData(@Nonnull CompoundTag compound) {
    }

    @Override
    protected void addAdditionalSaveData(@Nonnull CompoundTag compound) {
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Nullable
    @Override
    public ItemStack getPickResult() {
        return this.parentMob.getPickResult();
    }

    /**
     * Called when the entity is attacked.
     */
    @Override
    public boolean hurt(@Nonnull DamageSource source, float amount) {
        return this.isInvulnerableTo(source) ? false : this.parentMob.hurt(source, amount);
    }

    /**
     * Returns {@code true} if Entity argument is equal to this Entity
     */
    @Override
    public boolean is(@Nonnull Entity entity) {
        return this == entity || this.parentMob == entity;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(@Nonnull ServerEntity entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EntityDimensions getDimensions(@Nonnull Pose pose) {
        return this.size;
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }
}

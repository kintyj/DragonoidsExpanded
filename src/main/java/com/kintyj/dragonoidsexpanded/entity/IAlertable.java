package com.kintyj.dragonoidsexpanded.entity;

import net.minecraft.world.entity.LivingEntity;

public interface IAlertable {
    public abstract void onAlert(LivingEntity entity, LivingEntity target);
}

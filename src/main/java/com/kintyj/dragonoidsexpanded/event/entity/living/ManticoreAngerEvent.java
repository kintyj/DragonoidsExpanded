package com.kintyj.dragonoidsexpanded.event.entity.living;

import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class ManticoreAngerEvent extends LivingEvent implements ICancellableEvent{
    private final Player player;

    public ManticoreAngerEvent(EnderMan enderman, Player player) {
        super(enderman);
        this.player = player;
    }

    /**
     * The player that is being checked.
     */
    public Player getPlayer() {
        return player;
    }

    @Override
    public EnderMan getEntity() {
        return (EnderMan) super.getEntity();
    }
}

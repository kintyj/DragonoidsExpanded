package com.kintyj.dragonoidsexpanded.event.entity.living;

import com.kintyj.dragonoidsexpanded.entity.Manticore;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class ManticoreAngerEvent extends LivingEvent implements ICancellableEvent{
    private final Player player;

    public ManticoreAngerEvent(Manticore manticore, Player player) {
        super(manticore);
        this.player = player;
    }

    /**
     * The player that is being checked.
     */
    public Player getPlayer() {
        return player;
    }

    @Override
    public Manticore getEntity() {
        return (Manticore) super.getEntity();
    }
}

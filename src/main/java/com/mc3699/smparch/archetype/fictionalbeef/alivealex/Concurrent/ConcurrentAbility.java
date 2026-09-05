package com.mc3699.smparch.archetype.fictionalbeef.alivealex.Concurrent;


import net.mc3699.provenance.ability.foundation.AmbientAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class ConcurrentAbility extends AmbientAbility {
    @Override
    public void tick(ServerPlayer player) {

    }

    @Override
    public boolean canExecute(ServerPlayer player) {
        return true;
    }

    @Override
    public Component getName() {
        return Component.literal("Concurrent");
    }
}

package com.mc3699.smparch.archetype.fictionalbeef.xander.DemonPresence;

import net.mc3699.provenance.ability.foundation.AmbientAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class DemonPresencePassive extends AmbientAbility {


    @Override
    public boolean canExecute(ServerPlayer player) {
        return true;
    }

    @Override
    public Component getName() {
        return Component.literal("DemonPresence");
    }

    @Override
    public void tick(ServerPlayer arg0) {
    }
    
}

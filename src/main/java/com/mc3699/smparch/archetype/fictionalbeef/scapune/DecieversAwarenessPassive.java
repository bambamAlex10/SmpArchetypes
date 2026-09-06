package com.mc3699.smparch.archetype.fictionalbeef.scapune;

import net.mc3699.provenance.ability.foundation.AmbientAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class DecieversAwarenessPassive extends AmbientAbility {


    @Override
    public boolean canExecute(ServerPlayer player) {
        return true;
    }

    @Override
    public Component getName() {
        return Component.literal("AccesoMorendo");
    }

    @Override
    public void tick(ServerPlayer arg0) {
    }
    
}

package com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidCasino;

import net.mc3699.provenance.ability.foundation.AmbientAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class VoidCasinoAmbient extends AmbientAbility {

    @Override
    public void tick(ServerPlayer player) {
    }

    @Override
    public boolean canExecute(ServerPlayer player) {
        return true;
    }

    @Override
    public Component getName() {
        return Component.literal("void_casino");
    }
    
}
//todo needs either visual or gambling sfx
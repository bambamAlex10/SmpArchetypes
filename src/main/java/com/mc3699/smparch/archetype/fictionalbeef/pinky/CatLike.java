package com.mc3699.smparch.archetype.fictionalbeef.pinky;

import net.mc3699.provenance.ability.foundation.AmbientAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class CatLike extends AmbientAbility {

    @Override
    public void tick(ServerPlayer player) {
        player.fallDistance = 0;
    }

    @Override
    public boolean canExecute(ServerPlayer player) {
        return player.fallDistance>0;
    }

    @Override
    public Component getName() {
        return Component.literal("Cat Like");
    }

}

package com.mc3699.smparch.archetype.tbs.blox;

import com.mc3699.smparch.generic_abilities.DashAbility;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class BloxDashAbility extends DashAbility {

    @Override
    public int getCooldown() {
        return 20;
    }

    @Override
    public boolean canExecute(ServerPlayer player) {
        return BloxArchetype.checkArmor(player) && player.onGround();
    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.fromNamespaceAndPath("minecraft","textures/mob_effect/jump_boost.png");
    }
}

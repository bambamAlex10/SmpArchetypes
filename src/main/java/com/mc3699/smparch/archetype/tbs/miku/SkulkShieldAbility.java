package com.mc3699.smparch.archetype.tbs.miku;

import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class SkulkShieldAbility extends BaseAbility {
    @Override
    public float getUseCost() {
        return 1;
    }

    @Override
    public int getCooldown() { return 20*40; }

    @Override
    public Component getName() {
        return Component.literal("Skulk Shield");
    }

    // "Summons a 5x3x1 barrier of Sculk Blocks 2 blocks in front of the user"
    // MAKE SURE THIS DOES NOT GRIEF ANYTHING

    @Override
    public boolean canExecute(ServerPlayer serverPlayer) {
        return true;
    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.fromNamespaceAndPath("minecraft","textures/block/sculk.png");
    }
}
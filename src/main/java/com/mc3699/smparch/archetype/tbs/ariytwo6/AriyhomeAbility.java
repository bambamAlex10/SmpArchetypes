package com.mc3699.smparch.archetype.ariytwo6;

import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class AriyhomeAbility extends BaseAbility {
    @Override
    public float getUseCost() {
        return 0;
    }

    @Override
    public Component getName() {
        return Component.literal("Home Coming");
    }

    @Override
    public boolean canExecute(ServerPlayer serverPlayer) {
        return true;
    }

    @Override
    public int getCooldown() {
        return 30*20 ;
    }

    @Override
    public void execute(ServerPlayer player) {
        BlockPos spawnPos = player.getRespawnPosition();
        double x = spawnPos.getX();
        double y = spawnPos.getY();
        double z = spawnPos.getZ();
        player.teleportTo(x,y,z);

    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.withDefaultNamespace("textures/item/orange_dye.png");
    }
}

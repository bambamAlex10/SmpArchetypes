package com.mc3699.smparch.archetype.ariytwo6;

import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class WaterjumpAbility extends BaseAbility {
    @Override
    public float getUseCost() {
        return 4;
    }

    @Override
    public Component getName() {
        return Component.literal("Water Jump");
    }

    @Override
    public boolean canExecute(ServerPlayer serverPlayer) {
        return serverPlayer.isInWater() && serverPlayer.isCrouching();

    }

    @Override
    public void execute(ServerPlayer player) {
        super.execute(player);
        player.setDeltaMovement(0,100,0);
        player.hurtMarked=true;
    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.withDefaultNamespace("textures/item/water_bucket.png");
    }
}

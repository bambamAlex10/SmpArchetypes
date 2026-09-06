package com.mc3699.smparch.archetype.emberflame65;

import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class EmberSustainedFlight extends BaseAbility {
    @Override
    public float getUseCost() {
        return 0.5f;
    }

    @Override
    public Component getName() {
        return Component.literal("Sustained Flight");
    }

    @Override
    public boolean canExecute(ServerPlayer serverPlayer) {
        return !EmberAbilityUtils.isWet(serverPlayer);
    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.withDefaultNamespace("textures/item/nether_star.png");
    }

    @Override
    public int getCooldown() {
        return 7500;
    }

    @Override
    public void execute(ServerPlayer player) {
        super.execute(player);
        if (player.getAbilities().mayfly) {
            setFlight(player, false);
        } else {
            setFlight(player, true);
            EmberAbilityUtils.activateFlying(player);
        }
    }

    private void setFlight(ServerPlayer player, boolean enabled) {
        player.getAbilities().mayfly = enabled;
        player.onUpdateAbilities();
    }

    @Override
    public void backgroundTick(ServerPlayer serverPlayer) {
        super.backgroundTick(serverPlayer);
        if (EmberAbilityUtils.isWet(serverPlayer) || !EmberAbilityUtils.isFlying(serverPlayer)) {
            serverPlayer.getAbilities().flying = false;
            setFlight(serverPlayer, false);
        }
    }
}

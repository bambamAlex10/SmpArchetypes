package com.mc3699.smparch.archetype.emberflame65;

import com.mc3699.smparch.SMPArch;
import net.mc3699.provenance.ability.foundation.ToggleAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.gameevent.GameEvent;

public class EmberFlight extends ToggleAbility {


    @Override
    public float getUseCost() {
        return 0;
    }

    @Override
    public Component getName() {
        return Component.literal("Flight");
    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.withDefaultNamespace("textures/item/elytra.png");
    }

    @Override
    public boolean canExecute(ServerPlayer serverPlayer) {
        return !serverPlayer.onGround() && !EmberAbilityUtils.isSoaked(serverPlayer);
    }

    @Override
    protected void onToggle(ServerPlayer player, boolean enabled) {
        super.onToggle(player, enabled);
        if (!enabled) {
            player.stopFallFlying();
        }
    }

    @Override
    public void tick(ServerPlayer serverPlayer) {
        if (isEnabled(serverPlayer, ResourceLocation.fromNamespaceAndPath(SMPArch.MODID, "flight"))) {
            if (serverPlayer.onGround()) {
                disableSelf(serverPlayer);
            } else {
                serverPlayer.startFallFlying();
            }
        }
    }
}

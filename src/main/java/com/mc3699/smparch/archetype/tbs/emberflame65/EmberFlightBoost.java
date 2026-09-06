package com.mc3699.smparch.archetype.emberflame65;

import com.mc3699.smparch.SMPArch;
import net.mc3699.provenance.ProvenanceDataHandler;
import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class EmberFlightBoost extends BaseAbility {
    @Override
    public float getUseCost() {
        return 0;
    }

    @Override
    public Component getName() {
        return Component.literal("Flight Boost");
    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.withDefaultNamespace("textures/item/firework_rocket.png");
    }

    @Override
    public boolean canExecute(ServerPlayer serverPlayer) {
        return !EmberAbilityUtils.isWet(serverPlayer) && ProvenanceDataHandler.isAbilityEnabled(serverPlayer,
                ResourceLocation.fromNamespaceAndPath(SMPArch.MODID, "flight"));
    }

    @Override
    public void execute(ServerPlayer player) {
        super.execute(player);
        Vec3 look = player.getLookAngle();
        double speed = 2.2;
        player.setDeltaMovement(look.x * speed, look.y * speed, look.z * speed);
        player.hurtMarked = true;
    }


}

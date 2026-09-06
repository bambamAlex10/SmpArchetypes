package com.mc3699.smparch.archetype.tbs.john_ultrakill;

import com.mc3699.smparch.generic_abilities.DashAbility;
import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class UltrakillDashAbility extends BaseAbility {
    @Override
    public float getUseCost() {
        return 0;
    }

    @Override
    public Component getName() {
        return Component.literal("Instant Propulsion System");
    }

    @Override
    public boolean canExecute(ServerPlayer serverPlayer) {
        return true;
    }

    @Override
    public void execute(ServerPlayer player) {
        super.execute(player);
        Vec3 lookDir = player.getLookAngle();

        Vec3 dashMotion = lookDir.multiply(0.9f, 0.5f, 0.9f);
        player.setDeltaMovement(dashMotion);
        player.hurtMarked = true;
        player.fallDistance = 0;
    }

    @Override
    public int getCooldown() {
        return 20;
    }
}

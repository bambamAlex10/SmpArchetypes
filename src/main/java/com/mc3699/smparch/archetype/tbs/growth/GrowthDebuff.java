package com.mc3699.smparch.archetype.tbs.growth;

import net.mc3699.provenance.ability.foundation.AmbientAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.Blocks;

public class GrowthDebuff extends AmbientAbility {
    @Override
    public void tick(ServerPlayer serverPlayer) {
        serverPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10, 1));
    }

    @Override
    public Component getName() {
        return Component.literal("Born not of water or sand");
    }

    @Override
    public boolean canExecute(ServerPlayer serverPlayer) {
        ServerLevel serverLevel = serverPlayer.serverLevel();
        return serverPlayer.isInWater() || serverLevel.getBlockState(serverPlayer.getBlockPosBelowThatAffectsMyMovement()).is(Blocks.SAND) || serverLevel.getBlockState(serverPlayer.getBlockPosBelowThatAffectsMyMovement()).is(Blocks.RED_SAND);
    }
}

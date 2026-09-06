package com.mc3699.smparch.archetype.ariytwo6;

import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SimpleExplosionDamageCalculator;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class GroundpunchAbility extends BaseAbility {

    int slamTicks = 0;
    boolean isSlamming = false;
    ServerLevel serverLevel;

    @Override
    public float getUseCost() {
        return 2;
    }

    @Override
    public void execute(ServerPlayer player) {
        super.execute(player);
        isSlamming = true;
        serverLevel = (ServerLevel) player.level();
    }

    @Override
    public void backgroundTick(ServerPlayer serverPlayer) {
        super.backgroundTick(serverPlayer);
        if(serverPlayer.onGround() && serverLevel != null)
        {
            if(slamTicks > 2) {
                serverPlayer.setDeltaMovement(serverPlayer.getDeltaMovement().add(0,slamTicks*0.1f,0));
                serverPlayer.hurtMarked = true;
                serverLevel.explode(
                        serverPlayer, null,
                        new SimpleExplosionDamageCalculator(false, false, Optional.of(slamTicks*0.3f), Optional.empty()),
                        serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(),
                        8.0F, false,
                        Level.ExplosionInteraction.TRIGGER,
                        ParticleTypes.GUST_EMITTER_LARGE,
                        ParticleTypes.GUST_EMITTER_LARGE,
                        SoundEvents.WIND_CHARGE_BURST
                );
            }
            slamTicks = 0;
            isSlamming = false;
            return;
        }

        if(isSlamming) {
            serverPlayer.setDeltaMovement(serverPlayer.getDeltaMovement().add(0,-0.5f,0));
            serverPlayer.hurtMarked = true;
            serverPlayer.fallDistance = 0;
            slamTicks++;
        }
    }

    @Override
    public Component getName() {
        return Component.literal("Slam Test");
    }

    @Override
    public boolean canExecute(ServerPlayer serverPlayer) {
        return true;
    }

    @Override
    public int getColor() {
        return 0xFFA500;
    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.withDefaultNamespace("textures/item/wind_charge.png");
    }
}
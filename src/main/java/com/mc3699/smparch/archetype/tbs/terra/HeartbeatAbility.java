package com.mc3699.smparch.archetype.tbs.terra;

import com.mc3699.smparch.registry.SMPSounds;
import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.mc3699.provenance.util.ProvScheduler;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SimpleExplosionDamageCalculator;
import org.apache.logging.log4j.core.jmx.Server;
import org.joml.Quaterniondc;

import java.util.Optional;
import java.util.function.Function;


public class HeartbeatAbility extends BaseAbility {

    ExplosionDamageCalculator EXPLOSION_DAMAGE_CALCULATOR = new SimpleExplosionDamageCalculator(false, false, Optional.of(3f), Optional.empty());

    @Override
    public float getUseCost() {
        return 0;
    }

    @Override
    public Component getName() {
        return Component.literal("Heartbeat");
    }

    @Override
    public boolean canExecute(ServerPlayer serverPlayer) {
        return true;
    }

    @Override
    public void execute(ServerPlayer player) {
        ServerLevel serverLevel = player.serverLevel();

        serverLevel.playSound(null, player.getBlockPosBelowThatAffectsMyMovement().above(1), SMPSounds.HEARTBEAT.value(), SoundSource.PLAYERS, 8, 1);

        ProvScheduler.schedule(24, () -> {
            serverLevel.explode(player, null, EXPLOSION_DAMAGE_CALCULATOR, player.getX(), player.getY(), player.getZ(), 12.0F, false, Level.ExplosionInteraction.TRIGGER, ParticleTypes.GUST_EMITTER_LARGE, ParticleTypes.GUST_EMITTER_LARGE, SoundEvents.WIND_CHARGE_BURST);
        });
    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.fromNamespaceAndPath("minecraft", "textures/mob_effect/regeneration.png");
    }

    @Override
    public int getCooldown() {
        return 120 * 20;
    }
}

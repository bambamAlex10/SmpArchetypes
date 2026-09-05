package com.mc3699.smparch.archetype.fictionalbeef.bigmanrake.Vibrato;

import java.time.Instant;
import java.util.Optional;
import java.util.Random;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.archetype.fictionalbeef.bigmanrake.BigManRakeAttachments;
import com.mc3699.smparch.archetype.fictionalbeef.bigmanrake.BigManRakeSounds;
import com.mc3699.smparch.registry.SMPAttachments;
import com.mc3699.smparch.registry.SMPParticles;
import com.mc3699.smparch.registry.SMPSounds;

import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class VibratoAbility extends BaseAbility {
    private static Random randomPitch = new Random();

    @Override
    public boolean canExecute(ServerPlayer Player) {
        return true;
    }

    @Override
    public int getCooldown() {
        return 15*20;
    }

    @Override
    public Component getName() {
        return Component.literal("Vibrato");
    }

    @Override
    public float getUseCost() {
        return 1f;
    }

    public void execute(ServerPlayer player) {
        player.setData(BigManRakeAttachments.VIBRATO,Instant.now());
        Level level = player.level();

        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(SMPParticles.VIBRATO.get(), player.getX(), player.getY()+1.0f, player.getZ(), 40, 0.3, -0.3, 0.3, 0.01);
        }else {
            level.addParticle(SMPParticles.VIBRATO.get(), player.getX(), player.getY()+1.0, player.getZ(), 0.0D, 0.1D, 0.0D);
        }
        
        player.level().playSound(null, player.getBlockPosBelowThatAffectsMyMovement(), BigManRakeSounds.VIBRATO.value(), SoundSource.PLAYERS, 0.5f,  randomPitch.nextFloat() * (1.2f - .8f) + 1f);
        
    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.fromNamespaceAndPath(SMPArch.MODID, "textures/ability_icon/bigmanrake/vibrato.png");
    }

}

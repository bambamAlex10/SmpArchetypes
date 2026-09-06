package com.mc3699.smparch.archetype.fictionalbeef.xander.Beezlebub;

import java.util.Random;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.archetype.fictionalbeef.scapune.ScapuneAttachments;
import com.mc3699.smparch.archetype.fictionalbeef.scapune.ScapuneSounds;
import com.mc3699.smparch.registry.SMPAttachments;
import com.mc3699.smparch.registry.SMPParticles;
import com.mc3699.smparch.registry.SMPSounds;
import com.mc3699.smparch.util.DelayedTaskHandler;
import com.mojang.logging.LogUtils;

import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;

public class BeezlebubAbility extends BaseAbility {

    private static final double DASH_VELOCITY = 2d;
    private static Random randomPitch = new Random();

    private static int currentAmplifier = 0;

    @Override
    public boolean canExecute(ServerPlayer arg0) {
        int amplifier = arg0.hasEffect(MobEffects.DAMAGE_BOOST)
                ? (arg0.getEffect(MobEffects.DAMAGE_BOOST).getAmplifier() + 2)
                : 1;

        arg0.setData(ScapuneAttachments.STRENGTH_AMPLIFIER,amplifier);
        currentAmplifier = amplifier;

        return true;
    }

    @Override
    public Component getName() {
        return Component.literal("Beezle Bub");
    }

    @Override
    public float getUseCost() {
        return 3.0f;
    }

    @Override
    public int getCooldown() {


        int seconds = currentAmplifier == 1 ? 60 : (currentAmplifier == 2 ? 30 : (currentAmplifier == 3 ? 10 : 3));

        int duration = (((80 - seconds)/3) * 20) * 20;

        return duration;
    }

    @Override
    public void execute(ServerPlayer player) {
        Level level = player.level();

        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(SMPParticles.QUEENBEE.get(), player.getX(), player.getY(), player.getZ(), 100,
                    0.3, -0.3, 0.3, 0.01);
        } else {
            level.addParticle(SMPParticles.QUEENBEE.get(), player.getX(), player.getY(), player.getZ(), 0.0D, 0.1D,
                    0.0D);
        }

        player.level().playSound(null, player.getBlockPosBelowThatAffectsMyMovement(), ScapuneSounds.QUEENBEE.value(),
                SoundSource.PLAYERS, .9f, randomPitch.nextFloat() * (1.2f - .8f) + 1f);

        int amplifier = player.getData(ScapuneAttachments.STRENGTH_AMPLIFIER);
        double seconds = amplifier == 1.0 ? 45.0 : (amplifier == 2.0 ? 25.0 : (amplifier == 3.0 ? 10.0 : 3.0));

        currentAmplifier = amplifier;

        DelayedTaskHandler.scheduleDelayed(20 * 1, () -> {
            // player.setHealth(Math.clamp(player.getHealth() - (5.0f * 2f), 0.1f, 500f));

            if (Math.random()*2 >= 1) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, (int) ((seconds/2.0) * 20.0), amplifier - 1), player);
            } else {
            player.addEffect(new MobEffectInstance(MobEffects.WITHER, (int) ((seconds/2.0) * 20.0), amplifier - 1), player);
            }
            
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, (int) (seconds * 20), amplifier - 1), player);

            // if (player.hasEffect(MobEffects.DAMAGE_BOOST) &&
            // player.getEffect(MobEffects.DAMAGE_BOOST).getAmplifier() == 2) {
            // player.addEffect(new
            // MobEffectInstance(MobEffects.DAMAGE_BOOST,3*20,3),player);
            // } else {
            // player.addEffect(new
            // MobEffectInstance(MobEffects.DAMAGE_BOOST,5*20,2),player);
            // }
        });

    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.fromNamespaceAndPath(SMPArch.MODID, "textures/ability_icon/scapune/decieversfury.png");
    }

}

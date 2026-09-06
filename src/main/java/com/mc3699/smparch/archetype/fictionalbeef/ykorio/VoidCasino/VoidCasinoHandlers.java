package com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidCasino;

import net.mc3699.provenance.ProvenanceDataHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Random;
import java.util.random.RandomGenerator;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.archetype.fictionalbeef.alivealex.SalvationAbility;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidCasinoAmbient;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidEssenceAbility;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.YkorioAttachments;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.YkorioSounds;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidPresence.VoidPresenceHandlers;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidPresence.VoidBlocks.PresenceBlock.Registration.VoidPresenceBlock;
import com.mc3699.smparch.registry.SMPAttachments;
import com.mc3699.smparch.registry.SMPBlocks;
import com.mc3699.smparch.util.DelayedTaskHandler;
import com.mojang.logging.LogUtils;

@EventBusSubscriber(modid = SMPArch.MODID)
public class VoidCasinoHandlers {

    public static float HealthDebtAmount = 7.0f;
    public static int HealthDebtTime = 30; // seconds

    public static void goodRoll(Level level, Player attackingPlayer, LivingEntity gettingAttacked) {
        float flush = level.getRandom().nextFloat();

        if (flush > 0f && flush < 0.2f) {
            attackingPlayer.heal(5f);
        } else if (flush > .2f && flush < .25f) {
            gettingAttacked.addEffect(new MobEffectInstance(MobEffects.WITHER, 15 * 20, 0));
        } else if (flush > .25f && flush < .3f) {
            attackingPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 1));
        } else if (flush > .3f && flush < .35f) {
            if (attackingPlayer instanceof ServerPlayer attackingServerPlayer) {
                VoidEssenceAbility.staticExecute(attackingServerPlayer);
            } else {
                gettingAttacked.heal(20f);
            }

        } else if (flush > .35f && flush < .4f) {
            attackingPlayer.getFoodData().setSaturation(40f);
            attackingPlayer.getFoodData().setFoodLevel(20);
            attackingPlayer.setHealth(20f);
        } else if (flush > .4f && flush < .45f) {
            attackingPlayer.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 15 * 20, 1));
        } else if (flush > .45f && flush < .5f) {
            attackingPlayer.addEffect(new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE, 120 * 60 * 20, 4));
        } else if (flush > .5f && flush < .55f) {
            if (attackingPlayer instanceof ServerPlayer attackingServerPlayer) {
                SalvationAbility.staticExecute(attackingServerPlayer);
                DelayedTaskHandler.scheduleDelayed(20 * (SalvationAbility.ApplicationWindow / 950), () -> {
                    SalvationAbility.staticExecute(attackingServerPlayer);
                });
            }

        } else if (flush > .55f && flush < .552f) {
            gettingAttacked.kill();
        } else if (flush > .552f && flush < .6f) {
            if (level instanceof ServerLevel level2) {
                level2.setDayTime(16000);
            }
        } else {
            attackingPlayer.heal(2f);
        }
    }

    public static void badRoll(Level level, Player attackingPlayer, LivingEntity gettingAttacked) {
        float flush = level.getRandom().nextFloat();

        if (flush > .0f && flush < .2f) {
            attackingPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 5 * 20, 1));
        } else if (flush > .2f && flush < .25f) {
            attackingPlayer.setHealth(Math.clamp(attackingPlayer.getHealth() - HealthDebtAmount, 0.5f, 20f));
            attackingPlayer.setData(YkorioAttachments.HEALTH_DEBT, HealthDebtTime * 20);
        } else if (flush > .25f && flush < .3f) {
            if (gettingAttacked instanceof Player gettingAttackedPlayer) {
                gettingAttackedPlayer.getFoodData().setSaturation(40f);
                gettingAttackedPlayer.getFoodData().setFoodLevel(20);
            }
            gettingAttacked.setHealth(20f);
        } else if (flush > .3f && flush < .35f) {
            if (gettingAttacked instanceof ServerPlayer serverAttacked) {
                VoidEssenceAbility.staticExecute(serverAttacked);
            } else {
                gettingAttacked.heal(20f);
            }
        } else if (flush > .35f && flush < .4f) {
            level.explode(attackingPlayer, (gettingAttacked.getX() + attackingPlayer.getX()) / 2,
                    (gettingAttacked.getY() + attackingPlayer.getY()) / 2,
                    (gettingAttacked.getZ() + attackingPlayer.getZ()) / 2, 10f,
                    net.minecraft.world.level.Level.ExplosionInteraction.NONE);
        } else if (flush > .4f && flush < .55f) {
            attackingPlayer.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 5 * 20, 0));
        } else if (flush > .55f && flush < .552f) {
            attackingPlayer.kill();
        } else if (flush > .552f && flush < .6) {
            attackingPlayer.getFoodData().setSaturation(0f);
        } else {
            attackingPlayer.hurt(new DamageSources(level.registryAccess()).sweetBerryBush(), 2f);
        }
    }

    @SubscribeEvent
    public static void onHit(LivingDamageEvent.Pre event) {

        Entity attackEntity = event.getSource().getEntity();
        LivingEntity dead = event.getEntity();

        if (!(attackEntity instanceof Player attackingPlayer)
                || ProvenanceDataHandler.getAmbientAbilities(attackingPlayer).stream()
                        .noneMatch(ability -> ability instanceof VoidCasinoAmbient)) {
            return;
        }

        float chanceIncrease;

        if (attackingPlayer.getAttackStrengthScale(0f) != 1) {
            chanceIncrease = .025f;
        } else {
            chanceIncrease = .05f;
        }


        if (VoidPresenceHandlers.isEntityIntersectingTargetBlock(attackingPlayer, attackingPlayer.level(),
                SMPBlocks.VOIDPRESENCEBLOCK.get(), SMPBlocks.VOIDBLOCK.get())) {
            chanceIncrease += .1f;
        }

        float flush = attackingPlayer.level().getRandom().nextFloat();

        if (flush > 0f && flush < chanceIncrease) {
            attackingPlayer.level().playSound(null, attackingPlayer.getOnPos(), YkorioSounds.GAMBLEWIN.value(),
                    SoundSource.PLAYERS, 1f, 1f);
            goodRoll(attackingPlayer.level(), attackingPlayer, dead);
        } else if (flush > chanceIncrease && flush < chanceIncrease * 2) {
            attackingPlayer.level().playSound(null, attackingPlayer.getOnPos(), YkorioSounds.GAMBLELOSE.value(),
                    SoundSource.PLAYERS, 1f, 1f);
            badRoll(attackingPlayer.level(), attackingPlayer, dead);
        }

    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        if (!player.hasData(YkorioAttachments.HEALTH_DEBT) || player.level().isClientSide) {
            return;
        }

        int currentTime = player.getData(YkorioAttachments.HEALTH_DEBT);

        if (currentTime - 1 <= 0) {
            player.level().playSound(null, player.getOnPos(), YkorioSounds.GAMBLEWIN.value(),
                    SoundSource.PLAYERS, 1f, 1f);
            player.removeData(YkorioAttachments.HEALTH_DEBT);
            player.setHealth(Math.clamp(player.getHealth() + HealthDebtAmount, 0f, 20f));
        } else {
            player.setData(YkorioAttachments.HEALTH_DEBT, currentTime - 1);
        }

    }

}

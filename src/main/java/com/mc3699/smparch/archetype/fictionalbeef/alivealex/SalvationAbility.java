package com.mc3699.smparch.archetype.fictionalbeef.alivealex;

import java.beans.EventHandler;
import java.lang.annotation.Target;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.archetype.fictionalbeef.alivealex.BloodofCovenant.BloodofCovenantHandlers;
import com.mc3699.smparch.archetype.fictionalbeef.alivealex.Salvation.SalvationHandlers;
import com.mc3699.smparch.archetype.fictionalbeef.alivealex.Salvation.SalvationHandlers.TargetDamage;
import com.mc3699.smparch.registry.SMPSounds;
import com.mc3699.smparch.util.DelayedTaskHandler;
import com.mojang.logging.LogUtils;

import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class SalvationAbility extends BaseAbility {

    public static int ApplicationWindow = 7000;
    private boolean JustApplied = false;

    @Override
    public boolean canExecute(ServerPlayer Player) {
        return true;
    }

    @Override
    public int getCooldown() {
        if (JustApplied) {
            return ((ApplicationWindow / 1000) * 10) * 20;
        } else {
            return (ApplicationWindow / 5000) * 20;
        }
    }

    @Override
    public Component getName() {
        return Component.literal("Salvation");
    }

    @Override
    public float getUseCost() {
        if (JustApplied) {
            return 3.0f;
        } else {
            return .5f;
        }
    }

    public static void staticExecute(ServerPlayer player) {
        if (player.hasData(AliveAlexAttachments.INSTANT2)) {
            player.removeData(AliveAlexAttachments.INSTANT2);
        }

        if (!player.hasData(AliveAlexAttachments.CAPTUREDDAMAGE)) {
            player.setData(AliveAlexAttachments.INSTANT2, Instant.now());
        } else {
            ServerLevel level = (ServerLevel) player.level();
            List<TargetDamage> datalist = player
                    .getData(AliveAlexAttachments.CAPTUREDDAMAGE.get());

            Map<UUID, Float> ListedEntities = new HashMap<>();

            for (TargetDamage data : datalist) {
                UUID uuid = data.targetId();
                LivingEntity target = (LivingEntity) level.getEntity(uuid);

                if ((target == null)) {
                    continue;
                }

                float CurrentDamage = data.damage();

                target.level().playSound(null, target.getBlockPosBelowThatAffectsMyMovement(),
                        AliveAlexSounds.TIMEPRETRIGGER.value(), SoundSource.PLAYERS, 0.5f, 1f);

                target.setData(AliveAlexAttachments.DAMAGE_LOADING, true);
                target.syncData(AliveAlexAttachments.DAMAGE_LOADING);

                ListedEntities.put(uuid, CurrentDamage);

            }

            player.removeData(AliveAlexAttachments.CAPTUREDDAMAGE);

            DelayedTaskHandler.scheduleDelayed(20 * 4, () -> {
                float multiplier = 1;
                boolean crit = false;
                if (player != null) {
                    player.removeData(AliveAlexAttachments.SALVATION);
                    player.syncData(AliveAlexAttachments.SALVATION);
                    multiplier = !BloodofCovenantHandlers.isholdingWeapon(player) ? -1 : 1;
                    if (player.fallDistance > 0.0F // Must be falling downwards
                            && !player.onGround() // Must be airborne
                            && !player.onClimbable() // Not on a ladder, vine, or scaffolding
                            && !player.isInWater() // Not submerged in water
                            && !player.hasEffect(MobEffects.BLINDNESS) // Blindness blocks crits entirely
                            && !player.isPassenger() // Cannot be riding a horse, boat, minecart, etc.
                            && !player.isFallFlying() // Cannot be gliding with an Elytra
                    // Weapon attack strength must be at 84.8% or higher (represented as a 0.0 to
                    // 1.0 float)
                            && player.getAttackStrengthScale(0.5F) > 0.848F) {
                        multiplier *= 1.5;
                        crit = true;
                    }
                }

                for (Map.Entry<UUID, Float> Data : ListedEntities.entrySet()) {
                    Float CurrentDamage = Data.getValue() * multiplier;
                    Entity target = level.getEntity(Data.getKey());

                    if (target == null) {
                        continue;
                    }

                    if (crit) {
                        // Broadcast the particles to all tracking clients
                        ServerLevel serverLevel = (ServerLevel) target.level();

                        serverLevel.sendParticles(
                                ParticleTypes.CRIT, // Particle type (use ENCHANTED_CRIT for magic sharpness particles)
                                target.getX(), target.getY() + 1.0, target.getZ(), // Base coordinates
                                100, // Count: total number of particles to spawn
                                0.2, 0.3, 0.2, // Speed/Offset X, Y, Z: random spread inside the target's hitbox
                                0.1 // Particle speed factor
                        );
                    }

                    if (CurrentDamage < 0) {

                        target.level().playSound(null, target.getBlockPosBelowThatAffectsMyMovement(),
                                AliveAlexSounds.HEALTIME.value(), SoundSource.PLAYERS, 0.5f, 1f);
                        if (target instanceof LivingEntity friend) {
                            friend.heal(multiplier * CurrentDamage);
                            if (player != null)
                                BloodofCovenantHandlers.applyfakeKB((LivingEntity) player, friend,
                                        multiplier * CurrentDamage);
                        }
                    } else {

                        target.level().playSound(null, target.getBlockPosBelowThatAffectsMyMovement(),
                                AliveAlexSounds.DIETIME.value(), SoundSource.PLAYERS, 0.5f, .8f);
                        if (player != null) {
                            target.hurt(target.damageSources().playerAttack(player), CurrentDamage);
                        } else {
                            target.hurt(target.damageSources().generic(), CurrentDamage);
                        }
                        ;
                    }

                    target.removeData(AliveAlexAttachments.SALVATION);
                    target.removeData(AliveAlexAttachments.DAMAGE_LOADING);
                    target.syncData(AliveAlexAttachments.SALVATION);
                    target.syncData(AliveAlexAttachments.DAMAGE_LOADING);
                }

            });
        }
    }

    public void execute(ServerPlayer player) {
        if (player.hasData(AliveAlexAttachments.INSTANT2)) {
            player.removeData(AliveAlexAttachments.INSTANT2);
        }

        if (!player.hasData(AliveAlexAttachments.CAPTUREDDAMAGE)) {
            JustApplied = false;
            player.setData(AliveAlexAttachments.INSTANT2, Instant.now());
        } else {
            JustApplied = true;
            ServerLevel level = (ServerLevel) player.level();

            List<TargetDamage> datalist = player
                    .getData(AliveAlexAttachments.CAPTUREDDAMAGE.get());

            // Map<UUID, Float> ListedEntities = new HashMap<>();

            // for (TargetDamage data : datalist) {
            //     UUID uuid = data.targetId();
            //     LivingEntity target = (LivingEntity) level.getEntity(uuid);

            //     if ((target == null)) {
            //         continue;
            //     }

            //     float CurrentDamage = data.damage();

            //     target.level().playSound(null, target.getBlockPosBelowThatAffectsMyMovement(),
            //             AliveAlexSounds.TIMEPRETRIGGER.value(), SoundSource.PLAYERS, 0.5f, 1f);

            //     target.setData(AliveAlexAttachments.DAMAGE_LOADING, true);
            //     target.syncData(AliveAlexAttachments.DAMAGE_LOADING);

            //     ListedEntities.put(uuid, CurrentDamage);

            // }

            player.removeData(AliveAlexAttachments.CAPTUREDDAMAGE);

            DelayedTaskHandler.scheduleDelayed(20 * 4, () -> {
                float multiplier = 1;
                boolean crit = false;
                if (player != null) {
                    player.removeData(AliveAlexAttachments.SALVATION);
                    player.syncData(AliveAlexAttachments.SALVATION);
                    multiplier = !BloodofCovenantHandlers.isholdingWeapon(player) ? -1 : 1;
                    if (player.fallDistance > 0.0F // Must be falling downwards
                            && !player.onGround() // Must be airborne
                            && !player.onClimbable() // Not on a ladder, vine, or scaffolding
                            && !player.isInWater() // Not submerged in water
                            && !player.hasEffect(MobEffects.BLINDNESS) // Blindness blocks crits entirely
                            && !player.isPassenger() // Cannot be riding a horse, boat, minecart, etc.
                            && !player.isFallFlying() // Cannot be gliding with an Elytra
                    // Weapon attack strength must be at 84.8% or higher (represented as a 0.0 to
                    // 1.0 float)
                            && player.getAttackStrengthScale(0.5F) > 0.848F) {
                        multiplier *= 1.5;
                        crit = true;
                    }
                }

                for (TargetDamage Data : datalist) {
                    Float CurrentDamage = Data.damage() * multiplier;
                    Entity target = level.getEntity(Data.targetId());

                    if (!(target instanceof LivingEntity friend)) {
                        LogUtils.getLogger().debug("nope!");
                        continue;
                    }

                    if (crit) {
                        // Broadcast the particles to all tracking clients
                        ServerLevel serverLevel = (ServerLevel) target.level();

                        serverLevel.sendParticles(
                                ParticleTypes.CRIT, // Particle type (use ENCHANTED_CRIT for magic sharpness particles)
                                target.getX(), target.getY() + 1.0, target.getZ(), // Base coordinates
                                100, // Count: total number of particles to spawn
                                0.2, 0.3, 0.2, // Speed/Offset X, Y, Z: random spread inside the target's hitbox
                                0.1 // Particle speed factor
                        );
                    }

                    if (CurrentDamage < 0) {

                        target.level().playSound(null, target.getBlockPosBelowThatAffectsMyMovement(),
                                AliveAlexSounds.HEALTIME.value(), SoundSource.PLAYERS, 0.5f, 1f);
                        LogUtils.getLogger().debug("HEAL!!");
                            friend.heal(multiplier * CurrentDamage);
                            if (player != null)
                                BloodofCovenantHandlers.applyfakeKB((LivingEntity) player, friend,
                                        multiplier * CurrentDamage);

                    } else {

                        LogUtils.getLogger().debug("DAMAGE!!");
                        target.level().playSound(null, target.getBlockPosBelowThatAffectsMyMovement(),
                                AliveAlexSounds.DIETIME.value(), SoundSource.PLAYERS, 0.5f, .8f);
                        if (player != null) {
                            friend.hurt(target.damageSources().playerAttack(player), CurrentDamage);
                        } else {
                            friend.hurt(target.damageSources().generic(), CurrentDamage);
                        }
                        ;
                    }

                    target.removeData(AliveAlexAttachments.SALVATION);
                    target.removeData(AliveAlexAttachments.DAMAGE_LOADING);
                    target.syncData(AliveAlexAttachments.SALVATION);
                    target.syncData(AliveAlexAttachments.DAMAGE_LOADING);
                }

            });
        }
    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.fromNamespaceAndPath(SMPArch.MODID, "textures/ability_icon/alivealex/salvation.png");
    }
}

package com.mc3699.smparch.archetype.fictionalbeef.alivealex.Retribution;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;

import javax.naming.event.EventContext;
import javax.print.attribute.standard.MediaSize.Other;

import org.slf4j.Logger;

import com.mc3699.smparch.registry.SMPAbilities;
import com.mc3699.smparch.registry.SMPClient;
import com.mc3699.smparch.registry.SMPSounds;
import com.mc3699.smparch.util.DelayedTaskHandler;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.mc3699.provenance.ProvenanceDataHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.VanillaGameEvent;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import com.google.common.collect.Multimap;
import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.archetype.fictionalbeef.alivealex.AliveAlexAttachments;
import com.mc3699.smparch.archetype.fictionalbeef.alivealex.AliveAlexSounds;
import com.mc3699.smparch.archetype.fictionalbeef.alivealex.Salvation.SalvationUIOwner.RetributionCount;
import com.mc3699.smparch.packets.displayTargetDamage;

@EventBusSubscriber(modid = SMPArch.MODID)
public class RetributionHandlers {
    public static final long ParryWindow = 3500;
    public static final float RemainingHealth = 3f;
    public static final long LengthinVoid = 10000; // miliseconds

    @SubscribeEvent
    public static void onDamage2(LivingDamageEvent.Pre event) {
        LivingEntity eEntity = event.getEntity();
        // Entity target2Entity = event.getSource().getEntity();
        // float CurrentDamage = event.getNewDamage();

        if (eEntity.level().isClientSide || !(eEntity instanceof Player player)) {
            return;
        }

        if (player.hasData(AliveAlexAttachments.REAPING_BENEFITS.get())
                && player.getData(AliveAlexAttachments.REAPING_BENEFITS.get())) {
            event.setNewDamage(0.0f);
            return;
        }

        // if (!(target2Entity instanceof LivingEntity targetEntity) ) {
        //     return;
        // }

        if (player.hasData(AliveAlexAttachments.ALREADY_DEAD.get())) {
            Boolean before = false;

            if (player == null || player.level().isClientSide) {
                player.removeData(AliveAlexAttachments.ALREADY_DEAD.get());
                return;
            }

            if (Instant.now()
                    .isBefore(player.getData(AliveAlexAttachments.ALREADY_DEAD.get()).plusMillis(ParryWindow))) {
                player.removeData(AliveAlexAttachments.ALREADY_DEAD.get());
                before = true;
            } else {
                player.removeData(AliveAlexAttachments.ALREADY_DEAD.get());
            }

            if (before) {

                // Vec3 var =
                // targetEntity.position().subtract(targetEntity.getLookAngle().scale(1));

                // double heightAbove = 1.5;

                // double d0 = targetEntity.getX() - var.x;
                // double d1 = targetEntity.getY() - (var.y + heightAbove);
                // double d2 = targetEntity.getZ() - var.z;
                // double d3 = Math.sqrt(d0 * d0 + d2 * d2);

                // float yaw = (float) (Math.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;
                // float pitch = (float) (-(Math.atan2(d1, d3) * (180D / Math.PI)));

                // player.teleportTo(((ServerLevel) targetEntity.level()), var.x, var.y +
                // heightAbove, var.z,
                // Collections.emptySet(), yaw, pitch);
                player.setData(AliveAlexAttachments.REAPING_BENEFITS.get(), true);

                int ticks = ((int) (LengthinVoid / 1000)) * 20;


                DelayedTaskHandler.scheduleDelayed(ticks, () -> {
                    player.setData(AliveAlexAttachments.REAPING_BENEFITS.get(), false);
                });

                // targetEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 15, 1));
                // targetEntity.setDeltaMovement(new Vec3(0, 0, 0));

                player.level().playSound(null, player.getBlockPosBelowThatAffectsMyMovement(),
                        AliveAlexSounds.COUNTERACTIVE.value(), SoundSource.PLAYERS, 0.5f, 1f);
                player.level().playSound(null, player.getBlockPosBelowThatAffectsMyMovement(),
                        AliveAlexSounds.COUNTERWIN.value(), SoundSource.PLAYERS, 0.5f, 1f);
            }

            event.setNewDamage(0.0f);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {

        Player player = event.getEntity();

        if (player == null || player.level().isClientSide)
            return;

        if (!ProvenanceDataHandler.getAbilities(player).stream()
                .noneMatch(ability -> ability instanceof RetributionAbility)
                && player.hasData(AliveAlexAttachments.ALREADY_DEAD)) {

            Instant TimeLeft = player.getData(AliveAlexAttachments.ALREADY_DEAD.get());
            Instant now = Instant.now().minus(365 * 1, ChronoUnit.DAYS);

            if (now.isBefore(TimeLeft) && Instant.now()
                    .isAfter(player.getData(AliveAlexAttachments.ALREADY_DEAD.get()).plusMillis(ParryWindow))) {
                player.setData(AliveAlexAttachments.ALREADY_DEAD.get(), Instant.now().minus(5 * 365, ChronoUnit.DAYS));

                player.setData(AliveAlexAttachments.LAST_POS.get(), player.getPosition(0));
                player.level().playSound(null, player.getBlockPosBelowThatAffectsMyMovement(),
                        AliveAlexSounds.COUNTERACTIVE.value(), SoundSource.PLAYERS, 0.5f, 1f);
                player.level().playSound(null, player.getBlockPosBelowThatAffectsMyMovement(),
                        AliveAlexSounds.COUNTERLOSE.value(), SoundSource.PLAYERS, 0.5f, 1f);
                player.setHealth(RemainingHealth);

            }
            
            if (player.hasData(AliveAlexAttachments.LAST_POS.get())) {
                if (Instant.now().minus(5 * 365, ChronoUnit.DAYS).isAfter(TimeLeft.plusMillis(LengthinVoid))) {

                    Vec3 LastPos = player.getData(AliveAlexAttachments.LAST_POS.get());

                    player.level().playSound(null, player.getBlockPosBelowThatAffectsMyMovement(),
                            AliveAlexSounds.WELCOMEBACKVOID.value(), SoundSource.PLAYERS, 0.5f, 1f);
                    player.setHealth(RemainingHealth);

                    player.setDeltaMovement(new Vec3(0, 0, 0));
                    player.teleportTo(LastPos.x, LastPos.y, LastPos.z);

                    player.hurtMarked = true;

                    player.removeData(AliveAlexAttachments.ALREADY_DEAD.get());
                    player.removeData(AliveAlexAttachments.LAST_POS.get());

                } else {
                    player.teleportTo(0, player.level().getMinBuildHeight() - 10, 0);
                    ;
                    player.level().playSound(null, player.getOnPos(),
                            AliveAlexSounds.VOIDFAILWOW.value(), SoundSource.PLAYERS, .5f, 1.0f);

                    player.setHealth(RemainingHealth);
                    player.setAbsorptionAmount(0);
                }

            }
        }
    }
}

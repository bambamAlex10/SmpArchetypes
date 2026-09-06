package com.mc3699.smparch.archetype.fictionalbeef.alivealex.Concurrent;

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
import com.mc3699.smparch.registry.SMPAttachments;
import com.mc3699.smparch.registry.SMPClient;
import com.mc3699.smparch.registry.SMPSounds;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.mc3699.provenance.ProvenanceDataHandler;
import net.minecraft.client.Minecraft;
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
import com.mc3699.smparch.packets.displayTargetDamage;

@EventBusSubscriber(modid = SMPArch.MODID)
public class ConcurrentHandlers {

    @SubscribeEvent
    public static void onSetTarget(LivingChangeTargetEvent event) {
        LivingEntity yettoverify = event.getNewAboutToBeSetTarget();
        Player plr = null;

        if ((yettoverify instanceof Player plr2)) {
            plr = plr2;
        }

        if (plr == null) {
            return;
        }

        if (ProvenanceDataHandler.getAmbientAbilities(plr).stream()
                .noneMatch(ability -> ability instanceof ConcurrentAbility)) {
            return;
        }

        event.setCanceled(true);
        event.setNewAboutToBeSetTarget(null);
    }

    @SubscribeEvent
    public static void onVibration(VanillaGameEvent event) {
        GameEvent.Context EventContext = event.getContext();

        if (!(EventContext.sourceEntity() instanceof Player player)
                || ProvenanceDataHandler.getAmbientAbilities(player).stream()
                        .noneMatch(ability -> ability instanceof ConcurrentAbility)) {
            return;
        }

        if (!ProvenanceDataHandler.getAmbientAbilities(player).stream()
                .noneMatch(ability -> ability instanceof ConcurrentAbility)) {
            event.setCanceled(true);
        }
    }
    
}
package com.mc3699.smparch.archetype.fictionalbeef.bigmanrake.AccesoMorendo;

import java.rmi.AccessException;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Random;

import javax.swing.text.html.parser.Entity;

import com.google.common.eventbus.Subscribe;
import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.archetype.fictionalbeef.bigmanrake.AccesoMorendoAmbient;
import com.mc3699.smparch.archetype.fictionalbeef.bigmanrake.BigManRakeAttachments;
import com.mc3699.smparch.archetype.fictionalbeef.bigmanrake.BigManRakeSounds;
import com.mc3699.smparch.registry.SMPAbilities;
import com.mc3699.smparch.registry.SMPSounds;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.mc3699.provenance.ProvenanceDataHandler;
import net.mc3699.provenance.ProvenanceRegistries;
import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.mc3699.provenance.network.ProvNetworking;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import com.mojang.serialization.Codec;

@EventBusSubscriber(modid = SMPArch.MODID)
public class AccesoMorendoHandlers {

    private static final ArrayDeque<Runnable> DEFERRED = new ArrayDeque<>();

    @SubscribeEvent
    public static void onServerTickPost(ServerTickEvent.Post event) {
        Runnable r;
        while ((r = DEFERRED.poll()) != null) {
            try {
                r.run();
            } catch (Exception e) {
                LogUtils.getLogger().error("Deferred void-presence action failed", e);
            }
        }
    }

    public static final long AccesMorendoLength = 8500; // miliseconds
    public static final float RemainingHealth = 0.1f;

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent.Pre event) {
        float CurrentDamage = event.getNewDamage();

        if (event.getEntity() instanceof Player player && ProvenanceDataHandler.getAmbientAbilities(player).stream()
                .anyMatch(ability -> ability instanceof AccesoMorendoAmbient)) {
            if (!player.hasData(BigManRakeAttachments.ALREADY_DEAD.get())) {
                float finalDamage = CurrentDamage;
                float currentHealth = player.getHealth();

                if (currentHealth <= finalDamage) {
                    player.setData(BigManRakeAttachments.ALREADY_DEAD.get(), Instant.now());
                    CurrentDamage = 0.0f;

                    player.level().playSound(null, player.getBlockPosBelowThatAffectsMyMovement(),
                            BigManRakeSounds.MAEXTRODE.value(), SoundSource.PLAYERS, 0.2f, 1f);
                }
            } else if (Instant.now()
                    .isBefore(
                            player.getData(BigManRakeAttachments.ALREADY_DEAD.get()).plusMillis(AccesMorendoLength))) {
                CurrentDamage = 0.0f;
            }
        }

        event.setNewDamage(CurrentDamage);
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        if (player.level().isClientSide) {
            return;
        }

        ServerPlayer ServerPlayer = (ServerPlayer) player;

        Instant startingTime = player.getExistingDataOrNull(BigManRakeAttachments.ALREADY_DEAD.get());

        if (startingTime != null) {
            ;

            if (Instant.now().isBefore(startingTime.plusMillis(AccesMorendoLength))) {
                player.setAbsorptionAmount(0);
                player.setHealth(RemainingHealth);
            } else {
                player.removeData(BigManRakeAttachments.ALREADY_DEAD.get());

                DEFERRED.add(() -> {
                    // player.hurt(player.damageSources().fellOutOfWorld(), 600);

                    // if (player.isAlive()) {
                        player.setHealth(0f);
                        player.die(player.damageSources().fellOutOfWorld());
                    // }
                });

                if (player.hasData(BigManRakeAttachments.MOVEMENT)
                        && (player.getData(BigManRakeAttachments.MOVEMENT) == 4.3f)) {
                    player.setData(BigManRakeAttachments.MOVEMENT, 6.0f);
                }
            }
        }

    }
}

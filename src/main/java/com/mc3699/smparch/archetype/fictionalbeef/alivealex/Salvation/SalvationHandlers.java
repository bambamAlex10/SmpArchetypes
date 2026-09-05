package com.mc3699.smparch.archetype.fictionalbeef.alivealex.Salvation;

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
import com.mc3699.smparch.archetype.fictionalbeef.alivealex.AliveAlexAttachments;
import com.mc3699.smparch.packets.displayTargetDamage;

@EventBusSubscriber(modid = SMPArch.MODID)
public class SalvationHandlers {

    public record TargetDamage(float damage, UUID targetId) {
        public boolean isValid(ServerLevel level) {
            return level.getEntity(targetId) != null;
        }
    }

    public static Random randomPitch = new Random();

    public static ServerLevel level;

    public static boolean isholdingWeapon(Player attacker) {
        ItemStack CurrentlyHeld = attacker.getMainHandItem();

        ItemAttributeModifiers modifiers = CurrentlyHeld.getAttributeModifiers();
        Boolean hasAttackDamageModifer = false;

        for (ItemAttributeModifiers.Entry entry : modifiers.modifiers()) {
            if (entry.attribute().equals(Attributes.ATTACK_DAMAGE)) {
                if (entry.modifier().amount() > 0) {
                    hasAttackDamageModifer = true;
                    break;
                }
            }
        }

        return hasAttackDamageModifer;

    }

    public static void applyfakeKB(LivingEntity attacker, LivingEntity targetEntity, float Damage) {

        float f = attacker.getYRot() * ((float) Math.PI / 180F); // Convert degrees to radians
        double x = (double) (-Mth.sin(f));
        double z = (double) Mth.cos(f);

        if (Damage > 0) {
            targetEntity.knockback(.4f, x, z);
        }
        if (attacker.isSprinting()) {
            targetEntity.knockback(.5f, x, z);
            attacker.setDeltaMovement(attacker.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
        }
        return;
    }

    @SubscribeEvent
    public static void onDamage1(LivingIncomingDamageEvent event) {
        if (event.getEntity().hasData(AliveAlexAttachments.DAMAGE_LOADING)
                && event.getEntity().getData(AliveAlexAttachments.DAMAGE_LOADING)) {
            event.setCanceled(true);
            return;
        }

        if (event.getSource().getEntity() instanceof Player attacker
                && attacker.hasData(AliveAlexAttachments.INSTANT2)) {

            if (Instant.now().isAfter(
                    attacker.getData(AliveAlexAttachments.INSTANT2).plusMillis(SalvationAbility.ApplicationWindow))) {
                attacker.removeData(AliveAlexAttachments.INSTANT2);
                return;
            }
            // attacker.level().playSound(null,
            // attacker.getBlockPosBelowThatAffectsMyMovement(),
            // SMPSounds.TIMEPRETRIGGER.value(), SoundSource.PLAYERS, 0.5f, 1f);
            // attacker.removeData(AliveAlexAttachments.INSTANT2);
            // attacker.setData(AliveAlexAttachments.DAMAGE,
            // event.getContainer().getNewDamage());
            // attacker.setData(AliveAlexAttachments.TARGET,new
            // AliveAlexAttachments.TargetData()));

            List<TargetDamage> list = attacker.getData(AliveAlexAttachments.CAPTUREDDAMAGE.get());

            boolean found = false;
        float attackStrength = attacker.getAttackStrengthScale(0.5f); // 0.5f is partial tick
        
        // Calculate the actual damage based on attack cooldown
        float scaledDamage = event.getOriginalAmount() * attackStrength;
        
        // Only proceed if the scaled damage is significant
        if (scaledDamage <= 0.1f) {
            return; // Too weak, ignore this attack
        }

            for (int i = 0; i < list.size(); i++) {
                TargetDamage td = list.get(i);
                if (td.targetId().equals(event.getEntity().getUUID())) {
                    float newTotal = td.damage() + scaledDamage;
                    list.set(i, new TargetDamage(newTotal, event.getEntity().getUUID()));
                    found = true;
                    if (attacker instanceof ServerPlayer serverAttacker) {
                        PacketDistributor.sendToPlayer(serverAttacker, new displayTargetDamage(
                                scaledDamage, event.getEntity().getId()));
                    }
                    if (event.getEntity() instanceof ServerPlayer serverVictim) {
                    serverVictim.setData(AliveAlexAttachments.SALVATION, true);
                    serverVictim.syncData(AliveAlexAttachments.SALVATION);
                    }
                    break;
                }
            }

            if (!found) {
                list.add(new TargetDamage(scaledDamage, event.getEntity().getUUID()));
                if (attacker instanceof ServerPlayer serverAttacker) {
                    PacketDistributor.sendToPlayer(serverAttacker, new displayTargetDamage(
                            scaledDamage, event.getEntity().getId()));
                }
            }

            attacker.setData(AliveAlexAttachments.CAPTUREDDAMAGE, list);

            if (!attacker.getData(AliveAlexAttachments.SALVATION)) {
                attacker.setData(AliveAlexAttachments.SALVATION, true);
                attacker.syncData(AliveAlexAttachments.SALVATION);
            }

            event.setCanceled(true);
        } else if (event.getEntity().hasData(AliveAlexAttachments.LAST_POS.get())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerLoggedInEvent event) {
        Player player = event.getEntity();

        if (player.hasData(AliveAlexAttachments.CAPTUREDDAMAGE)) {

            if (ProvenanceDataHandler.getAbilities(player).stream().noneMatch(ability -> ability instanceof SalvationAbility)) {
                player.removeData(AliveAlexAttachments.CAPTUREDDAMAGE);
                return;
            }

            player.setData(AliveAlexAttachments.SALVATION, true);

            List<TargetDamage> datalist = player
                    .getData(AliveAlexAttachments.CAPTUREDDAMAGE.get());

            List<UUID> UUIDs = new ArrayList<>();

            for (TargetDamage targetD : datalist) {
                UUIDs.add(targetD.targetId());
                PacketDistributor.sendToPlayer((ServerPlayer) player, new displayTargetDamage(targetD.damage, event.getEntity().getId()));
            }

            for (ServerPlayer other : player.getServer().getPlayerList().getPlayers()) {
                if (UUIDs.contains(other.getUUID())) {
                    other.setData(AliveAlexAttachments.SALVATION,true);
                }
            }

            // }
        } else {
            for (ServerPlayer other : player.getServer().getPlayerList().getPlayers()) {
                if (other.hasData(AliveAlexAttachments.CAPTUREDDAMAGE)) {
                    for (TargetDamage target :
                        other.getData(AliveAlexAttachments.CAPTUREDDAMAGE)) {
                    if (target.targetId.equals(player.getUUID())) {
                        player.setData(AliveAlexAttachments.SALVATION,true);
                    }
                }
                }
            }

        }

    }
}

package com.mc3699.smparch.archetype.fictionalbeef.alivealex.BloodofCovenant;

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
import net.minecraft.world.item.Items;
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
import com.mc3699.smparch.archetype.fictionalbeef.alivealex.BloodofCovenantAmbient;
import com.mc3699.smparch.packets.displayTargetDamage;

@EventBusSubscriber(modid = SMPArch.MODID)
public class BloodofCovenantHandlers {

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

        if (CurrentlyHeld.is(Items.END_CRYSTAL) || CurrentlyHeld.is(Items.RESPAWN_ANCHOR)) {
            hasAttackDamageModifer = true;
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
    public static void onDamage2(LivingDamageEvent.Pre event) {
        LivingEntity eEntity = event.getEntity();
        Entity target2Entity = event.getSource().getEntity();
        float CurrentDamage = event.getNewDamage();

        if (eEntity.level().isClientSide) {
            return;
        }

        if (!(target2Entity instanceof LivingEntity targetEntity)) {
            return;
        }

        if (targetEntity instanceof Player attacker
                && ProvenanceDataHandler.getAmbientAbilities(attacker).stream()
                        .anyMatch(ability -> ability instanceof BloodofCovenantAmbient)) {
            if (isholdingWeapon(attacker)) {
                event.setNewDamage(CurrentDamage * 1.2f);
            } else {
                event.setNewDamage(CurrentDamage * -1f);
                eEntity.heal(CurrentDamage);
            }
        }

        // if (ApplyKB && targetEntity instanceof Player attacker) {
        // applyfakeKB(attacker,Entity,CurrentDamage);
        // }
    }

    


}

// if (player.level().isClientSide) {
// return;
// }
// Instant startingTime =
// player.getExistingDataOrNull(SMPAttachments.ALREADY_DEAD.get());
// if (startingTime == null)
// return;

// if (!Instant.now().isAfter(startingTime.plusMillis(LengthinVoid))) {
// player.setAbsorptionAmount(0);
// player.setHealth(RemainingHealth);
// } else {
// player.kill();
// }
// }
// }

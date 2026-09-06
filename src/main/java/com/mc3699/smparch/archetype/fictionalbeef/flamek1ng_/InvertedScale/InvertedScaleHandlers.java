package com.mc3699.smparch.archetype.fictionalbeef.flamek1ng_.InvertedScale;

import java.time.Duration;
import java.time.Instant;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.archetype.fictionalbeef.flamek1ng_.FlameK1ng_Attachments;
import com.mc3699.smparch.archetype.fictionalbeef.flamek1ng_.FlameK1ng_Sounds;
import com.mc3699.smparch.util.DelayedTaskHandler;
import com.mojang.logging.LogUtils;

import net.mc3699.provenance.Provenance;
import net.mc3699.provenance.ProvenanceDataHandler;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = SMPArch.MODID)
public class InvertedScaleHandlers {

    public static void startCounter(Player player, Instant time) {
        player.setData(FlameK1ng_Attachments.SCALEDURATION, time);
    }

    public static void superState(Player player, int duration) {
        player.level().playSound(null,
                player.getBlockPosBelowThatAffectsMyMovement(),
                FlameK1ng_Sounds.INVERTEDSCALESUCCESS.value(),
                SoundSource.PLAYERS, 1.1f, 1f);

        player.setData(FlameK1ng_Attachments.IN_SUPER_STATE, true);

        int durationinSeconds = duration * 2;

        if (durationinSeconds > 6) {
            for (LivingEntity plr : player.level().getNearbyEntities(LivingEntity.class,
                    TargetingConditions.forCombat(), player, AABB.ofSize(player.position(), 30, 30, 30))) {
                plr.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, durationinSeconds * 20, 0));
            }
        }

        DelayedTaskHandler.scheduleDelayed(durationinSeconds * 20, () -> {
            player.setData(FlameK1ng_Attachments.IN_SUPER_STATE, false);
        });

    }

    @SubscribeEvent
    public static void newDamageStarting(LivingIncomingDamageEvent Event) {

        if (!(Event.getEntity() instanceof Player player) || !player.hasData(FlameK1ng_Attachments.SCALEDURATION)) {
            return;
        }

        if (Instant.now().isBefore(player.getData(FlameK1ng_Attachments.SCALEDURATION))) {
            
            superState(player, (int) Duration
                    .between(Instant.now(), player.getData(FlameK1ng_Attachments.SCALEDURATION)).getSeconds());
                    Event.setAmount(0.0f);
            Event.setCanceled(true);
        }

        player.removeData(FlameK1ng_Attachments.SCALEDURATION);

    }

    @SubscribeEvent
    public static void strengthenState(LivingDamageEvent.Pre Event) {

        if ((Event.getEntity() instanceof Player player) && player.hasData(FlameK1ng_Attachments.IN_SUPER_STATE)
                && player.getData(FlameK1ng_Attachments.IN_SUPER_STATE)) {
            Event.setNewDamage(Event.getNewDamage() * .8f);
        } else if (Event.getContainer().getSource().getEntity() instanceof Player player
                && player.hasData(FlameK1ng_Attachments.IN_SUPER_STATE)
                && player.getData(FlameK1ng_Attachments.IN_SUPER_STATE)) {
            Event.setNewDamage(Event.getNewDamage() * 1.2f);
        }

    }

}

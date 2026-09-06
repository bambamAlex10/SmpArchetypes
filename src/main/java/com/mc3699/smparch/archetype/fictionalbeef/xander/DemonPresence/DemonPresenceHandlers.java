package com.mc3699.smparch.archetype.fictionalbeef.xander.DemonPresence;

import java.util.Optional;
import java.util.UUID;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.archetype.fictionalbeef.scapune.DecieversGraceAbility;
import com.mc3699.smparch.archetype.fictionalbeef.scapune.ScapuneAttachments;
import com.mc3699.smparch.archetype.fictionalbeef.scapune.DecieversGrace.DecieversGhost.DecieversGhostEntity;

import net.mc3699.provenance.ProvenanceDataHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import com.mojang.logging.LogUtils;

@EventBusSubscriber(modid = SMPArch.MODID)
public class DemonPresenceHandlers {

    // public static float decieverAwarenessIncrease = (1f/3f);
    // public static float decieverAwarenessDecrease = .0166f;
    // public static float decieverAwarenessMaximum = 10.0f;
    // public static float decieverAwarenessMinimum = 0.0f;

    @SubscribeEvent
    public static void onDamage2(LivingDamageEvent.Pre event) {
        // LivingEntity gettingAttackedEntity = event.getEntity();
        // Entity attackingEntity = event.getSource().getEntity();

        // if (attackingEntity == null || gettingAttackedEntity == null || attackingEntity.level().isClientSide) {
        //     return;
        // }
        // if (!(attackingEntity instanceof Player attackingPlayer)
        //         || !(gettingAttackedEntity instanceof Player gettingAttackedPlayer)) {
        //     return;
        // }

        // if (ProvenanceDataHandler.getAmbientAbilities(gettingAttackedPlayer).stream()
        //         .noneMatch(abilitiy -> abilitiy instanceof DecieversAwarenessPassive)) {
        //     return;
        // }

        // if (!attackingPlayer.hasData(ScapuneAttachments.DECIEVERS_AWARENESS)) {
        //     attackingPlayer.setData(ScapuneAttachments.DECIEVERS_AWARENESS, decieverAwarenessIncrease);
        // } else {
        //     float Current = attackingPlayer.getData(ScapuneAttachments.DECIEVERS_AWARENESS);
        //     attackingPlayer.setData(ScapuneAttachments.DECIEVERS_AWARENESS,
        //             Math.clamp(Current + decieverAwarenessIncrease, decieverAwarenessMinimum, decieverAwarenessMaximum));
        // }

        // attackingPlayer.syncData(ScapuneAttachments.DECIEVERS_AWARENESS);

    }

    // @SubscribeEvent
    // public static void onPlayerTick(PlayerTickEvent.Post event) {
    //     Player player = event.getEntity();

    //     if (!player.hasData(ScapuneAttachments.DECIEVERS_AWARENESS)) {
    //         return;
    //     }

    //     float Current = player.getData(ScapuneAttachments.DECIEVERS_AWARENESS);

    //     if ((Current - decieverAwarenessDecrease) <= 0f) {
    //         player.removeData(ScapuneAttachments.DECIEVERS_AWARENESS);
    //     } else {
    //         player.setData(ScapuneAttachments.DECIEVERS_AWARENESS,
    //                 Math.clamp(Current - decieverAwarenessDecrease, decieverAwarenessMinimum, decieverAwarenessMaximum));
    //     }

    //     if (Current % .05f == 0) {
    //         player.syncData(ScapuneAttachments.DECIEVERS_AWARENESS);
    //     }
    // }

    // @SubscribeEvent
    // public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
    // if (event.getEntity() instanceof DecieversGhostEntity clone) {
    // Player sourcePlayer = clone.getOwner();
    // if (sourcePlayer != null) {
    // CPMServerHelper.initialize();
    // UUID cloneUUID = clone.getUUID();
    // for (Player player : event.getLevel().players()) {
    // CPMServerHelper.syncModelToPlayer((ServerPlayer) player, clone,
    // sourcePlayer);
    // }
    // }
    // }
    // }

    // @SubscribeEvent
    // public static void onPlayerStartTracking(PlayerEvent.StartTracking event) {
    // if (event.getTarget() instanceof DecieversGhostEntity clone) {
    // ServerPlayer trackingPlayer = (ServerPlayer) event.getEntity();
    // Player sourcePlayer = clone.getOwner();
    // if (sourcePlayer != null) {
    // CPMServerHelper.initialize();
    // CPMServerHelper.syncModelToPlayer(trackingPlayer, clone, sourcePlayer);
    // }
    // }
    // }

}

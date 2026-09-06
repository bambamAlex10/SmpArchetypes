package com.mc3699.smparch.archetype.fictionalbeef.scapune.DecieversGrace;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.archetype.fictionalbeef.alivealex.ConcurrentAbility;
import com.mc3699.smparch.archetype.fictionalbeef.scapune.DecieversGraceAbility;
import com.mc3699.smparch.archetype.fictionalbeef.scapune.ScapuneAttachments;
import com.mc3699.smparch.archetype.fictionalbeef.scapune.DecieversGrace.DecieversGhost.DecieversGhostEntity;

import net.mc3699.provenance.ProvenanceDataHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import com.mojang.logging.LogUtils;

@EventBusSubscriber(modid = SMPArch.MODID)
public class DecieversGraceHandlers {

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        for (ServerPlayer Player : event.getServer().getPlayerList().getPlayers()) {
            if (ProvenanceDataHandler.getAbilities(Player).stream()
                    .noneMatch(ability -> ability instanceof DecieversGraceAbility)
                    || !Player.hasData(ScapuneAttachments.GHOST_UUID)) {
                continue;
            }

            Optional<UUID> ghostUuid = Player.getData(ScapuneAttachments.GHOST_UUID.get());

            ServerLevel level = (ServerLevel) Player.level();

            if (ghostUuid.isPresent() && level.getEntity(ghostUuid.get()) instanceof DecieversGhostEntity John) {

                int remaining = John.getLifeTicks();
                if (remaining > 0) {
                    remaining--;
                    if (remaining == 0) {
                        John.kill();
                        Player.removeData(ScapuneAttachments.GHOST_UUID.get());
                    } else {

                        John.setLifeTicks(remaining);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onSetTarget(LivingChangeTargetEvent event) {
        LivingEntity yettoverify = event.getNewAboutToBeSetTarget();

        if ((yettoverify instanceof Player plr)) {

            if (ProvenanceDataHandler.getAmbientAbilities(plr).stream()
                    .noneMatch(ability -> ability instanceof DecieversGraceAbility)
                    || (!plr.hasData(ScapuneAttachments.GHOST_UUID) && !plr.hasEffect(MobEffects.INVISIBILITY))) {
                return;
            }

            event.setCanceled(true);
        } 
        
        if (event.getEntity() instanceof Mob attacker && attacker instanceof Enemy) {
            List<DecieversGhostEntity> ghosts = attacker.level().getEntitiesOfClass(
                    DecieversGhostEntity.class,
                    attacker.getBoundingBox().inflate(24.0));

            if (!ghosts.isEmpty()) {
                DecieversGhostEntity closestGhost = ghosts.get(0);

                event.setNewAboutToBeSetTarget(closestGhost);
            }
        }

        // event.setNewAboutToBeSetTarget(plr.level().getPlayerByUUID(plr.getData(ScapuneAttachments.GHOST_UUID).get()));
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer Player))
            return;
        ServerLevel level = Player.serverLevel();

        if (ProvenanceDataHandler.getAbilities(Player).stream().noneMatch(
                ability -> ability instanceof DecieversGraceAbility)
                || !Player.hasData(ScapuneAttachments.GHOST_UUID)) {
            return;
        }

        Optional<UUID> ghostUuid = Player.getData(ScapuneAttachments.GHOST_UUID.get());
        Entity entity = level.getEntity(ghostUuid.get());
        if (entity instanceof DecieversGhostEntity ghost) {
            ghost.kill();
            Player.removeData(ScapuneAttachments.GHOST_UUID.get());
        }

    }
}

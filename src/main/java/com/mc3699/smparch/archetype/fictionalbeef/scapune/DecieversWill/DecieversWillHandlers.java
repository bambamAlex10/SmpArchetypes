package com.mc3699.smparch.archetype.fictionalbeef.scapune.DecieversWill;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.archetype.fictionalbeef.bigmanrake.BigManRakeAttachments;
import com.mc3699.smparch.archetype.fictionalbeef.bigmanrake.BigManRakeSounds;
import com.mc3699.smparch.archetype.fictionalbeef.bigmanrake.Concerto.ConcertoAbility;
import com.mc3699.smparch.archetype.fictionalbeef.scapune.ScapuneAttachments;
import com.mc3699.smparch.archetype.fictionalbeef.scapune.DecieversAwareness.DecieversAwarenessHandlers;
import com.mc3699.smparch.archetype.fictionalbeef.scapune.DecieversGrace.DecieversGraceAbility;
import com.mc3699.smparch.archetype.fictionalbeef.scapune.DecieversGrace.DecieversGhost.DecieversGhostEntity;
import com.mc3699.smparch.registry.SMPArchetypes;

import net.mc3699.provenance.ProvenanceDataHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.level.BlockEvent.BreakEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import com.mojang.logging.LogUtils;

@EventBusSubscriber(modid = SMPArch.MODID)
public class DecieversWillHandlers {

    static ResourceKey<Block> cage = ResourceKey.create(Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath("rake", "cages"));

    public static void clean(Player player) {
        player.removeData(ScapuneAttachments.WILL_MOMENT);
        player.removeData(ScapuneAttachments.WILL_STRENGTH);
        player.removeAllEffects();
    }

    public static boolean checkifShouldDestroy(Player player) {

        if (!player.hasData(ScapuneAttachments.WILL_MOMENT)) {
            return false;
        } else if (!player.hasData(ScapuneAttachments.WILL_STRENGTH)
                || player.getData(ScapuneAttachments.WILL_MOMENT)
                        .plusSeconds((long) (80f * player.getData(ScapuneAttachments.WILL_STRENGTH)))
                        .isBefore(Instant.now())) {
            clean(player);
            return true;
        } else {
            return false;
        }
    }

    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        net.minecraft.world.entity.Entity source = event.getSource().getEntity();

        if (!(source instanceof Player plr)) {
            return;
        }

        checkifShouldDestroy(plr);
    }

    // public static Block getCorrectBlock(Player plr) {
    // Block val = plr.level().registryAccess().registry(Registries.BLOCK).
    // (registry -> {
    // registry.getHolder(cage).ifPresentOrElse(holder -> {
    // val = holder.value();

    // }, () -> {

    // val = Blocks.OBSIDIAN;
    // });
    // });
    // =
    // return val;
    // }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        net.minecraft.world.entity.Entity source = event.getEntity();

        if (!(source instanceof Player plr) || plr.level().isClientSide) {
            return;
        }

        checkifShouldDestroy(plr);

        if (plr.hasData(ScapuneAttachments.WILL_MOMENT)) {

            for (Player opponent : plr.level().getServer().getPlayerList().getPlayers()) {
                if (opponent.equals(plr) || !(opponent.level().equals(plr.level()))) {
                    continue;
                }

                if ((plr.position().distanceTo(opponent.position())) < (8f
                        * plr.getData(ScapuneAttachments.WILL_STRENGTH))) {

                    float currentAwareness = opponent.hasData(ScapuneAttachments.DECIEVERS_AWARENESS)
                            ? opponent.getData(ScapuneAttachments.DECIEVERS_AWARENESS)
                            : 0;

                    opponent.setData(ScapuneAttachments.DECIEVERS_AWARENESS,
                            Math.clamp(currentAwareness + ((1.0f / 2.0f) * (3.5f / 20.0f)), .1f,
                                    DecieversAwarenessHandlers.decieverAwarenessMaximum));
                    opponent.syncData(ScapuneAttachments.DECIEVERS_AWARENESS);
                }
            }

            BlockPos startPos = plr.getOnPos();
            int replacedCount = 0;
            int maxTarget = 50;

            for (int x = -3; x <= 3 && replacedCount < maxTarget; x++) {
                for (int y = 0; y <= 0 && replacedCount < maxTarget; y++) {
                    for (int z = -3; z <= 3 && replacedCount < maxTarget; z++) {

                        BlockPos targetPos = startPos.offset(x, y, z);
                        BlockState currentState = plr.level().getBlockState(targetPos);

                        if (currentState.isAir()
                                || currentState.canBeReplaced()
                                || currentState.getDestroySpeed(plr.level(), targetPos) < 0f
                                || plr.level().getCapability(Capabilities.ItemHandler.BLOCK, targetPos, null) != null
                                || plr.blockActionRestricted(plr.level(), targetPos, plr.getAbilities().instabuild
                                        ? GameType.CREATIVE
                                        : GameType.SURVIVAL)
                                || !plr.level().getWorldBorder().isWithinBounds(targetPos)) {
                            continue;
                        }
                        ServerPlayer serverPlayer = (ServerPlayer) plr;

                        plr.level().registryAccess().registry(Registries.BLOCK).ifPresent(registry -> {
                            registry.getHolder(cage).ifPresentOrElse(holder -> {
                                safeBreak(serverPlayer, targetPos, holder.value().defaultBlockState());
                            },
                                    () -> {
                                        safeBreak(serverPlayer, targetPos, Blocks.OBSIDIAN.defaultBlockState());
                                    });
                        });

                        replacedCount++;
                    }
                }
            }
        }

    }

    public static boolean safeBreak(ServerPlayer serverPlayer, BlockPos targetPos, BlockState block) {

        BlockEntity blockEntity = serverPlayer.level().getBlockEntity(targetPos);

        if (blockEntity != null) {

            String className = blockEntity.getClass().getName().toLowerCase();

            if (className.contains("grave") || className.contains("tombstone") || className.contains("gravestone")) {

                try {
                    UUID ownerUUID = null;

                    try {
                        java.lang.reflect.Method method = blockEntity.getClass().getMethod("getOwnerUUID");
                        Object result = method.invoke(blockEntity);
                        if (result instanceof UUID) {
                            ownerUUID = (UUID) result;
                        }
                    } catch (NoSuchMethodException e) {

                    }

                    if (ownerUUID == null) {
                        try {
                            java.lang.reflect.Method method = blockEntity.getClass().getMethod("getOwner");
                            Object result = method.invoke(blockEntity);
                            if (result instanceof UUID) {
                                ownerUUID = (UUID) result;
                            } else if (result instanceof String) {
                                ownerUUID = UUID.fromString((String) result);
                            }
                        } catch (NoSuchMethodException e) {

                        }
                    }

                    if (ownerUUID == null) {
                        try {
                            java.lang.reflect.Method method = blockEntity.getClass().getMethod("getGraveOwner");
                            Object result = method.invoke(blockEntity);
                            if (result instanceof UUID) {
                                ownerUUID = (UUID) result;
                            }
                        } catch (NoSuchMethodException e) {

                        }
                    }

                    if (ownerUUID == null) {
                        CompoundTag tag = blockEntity.saveWithoutMetadata(serverPlayer.level().registryAccess());
                        String[] tagNames = { "owner", "Owner", "grave_owner", "player_uuid", "owner_uuid",
                                "graveOwner", "playerUUID" };
                        for (String tagName : tagNames) {
                            if (tag.contains(tagName)) {
                                try {
                                    ownerUUID = tag.getUUID(tagName);
                                    break;
                                } catch (Exception e) {
                                    try {
                                        ownerUUID = UUID.fromString(tag.getString(tagName));
                                        break;
                                    } catch (Exception e2) {

                                    }
                                }
                            }
                        }
                    }

                    if (ownerUUID != null && !ownerUUID.equals(serverPlayer.getUUID())) {
                        return false;
                    }

                    if (ownerUUID == null) {
                        return false;
                    }

                } catch (Exception e) {
                    if (!serverPlayer.hasPermissions(2)) {
                        return false;
                    }
                }
            }
        }

        serverPlayer.level().setBlockAndUpdate(targetPos, block);
        return true;
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerLoggedInEvent event) {
        net.minecraft.world.entity.Entity source = event.getEntity();

        if (!(source instanceof Player plr)) {
            return;
        }

        checkifShouldDestroy(plr);
    }
}

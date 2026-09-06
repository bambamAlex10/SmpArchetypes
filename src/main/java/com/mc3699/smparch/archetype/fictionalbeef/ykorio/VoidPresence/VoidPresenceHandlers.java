package com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidPresence;

import net.mc3699.provenance.ProvenanceDataHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.status.ServerStatus.Players;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.Tags.DamageTypes;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.time.Instant;
import java.util.ArrayDeque;

import net.neoforged.fml.common.EventBusSubscriber;
import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidPresenceAbility;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.YkorioAttachments;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.YkorioSounds;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidPresence.VoidBlocks.PresenceBlock.VoidPresenceBlockEntity;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidPresence.VoidBlocks.PresenceBlock.VoidPresenceTracker;
import com.mc3699.smparch.registry.SMPBlocks;
import com.mojang.logging.LogUtils;

@EventBusSubscriber(modid = SMPArch.MODID)
public class VoidPresenceHandlers {
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

    @SubscribeEvent
    public static void onLogin(PlayerLoggedInEvent event) {
        Player loggingIn = event.getEntity();

        if (!loggingIn.hasData(YkorioAttachments.PRESENCE_NOTICED)) {
            return;
        }

        Instant timeStarted = loggingIn.getData(YkorioAttachments.PRESENCE_NOTICED);

        if (Instant.now().isBefore(timeStarted.plusMillis(VoidPresenceAbility.LengthinMillis))) {
            return;
        }

        loggingIn.removeData(YkorioAttachments.PRESENCE_NOTICED);
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player auraFarmer = event.getEntity();

        if (auraFarmer.level().isClientSide)
            return;

        ServerLevel level = (ServerLevel) auraFarmer.level();

        if (!auraFarmer.hasData(YkorioAttachments.PRESENCE_NOTICED) || auraFarmer.level().isClientSide()) {
            return;
        }

        if (Instant.now().isAfter(auraFarmer.getData(YkorioAttachments.PRESENCE_NOTICED)
                .plusMillis(VoidPresenceAbility.LengthinMillis))) {
            auraFarmer.removeData(YkorioAttachments.PRESENCE_NOTICED);

            auraFarmer.level().playSound(null, auraFarmer.getOnPos(), YkorioSounds.SMOKEDISSIPATES.value(),
                    SoundSource.PLAYERS, 1f, 1f);
            return;
        }
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Pre event) {
        Entity entity = event.getEntity();
        Level level = entity.level();

        if (level.isClientSide) {
            return;
        }

        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }

        // ---------- existing tracker logic ----------
        if (!livingEntity.hasData(YkorioAttachments.VOID_PRESENCE_TRACKER)
                && !isEntityIntersectingTargetBlock(livingEntity, level, SMPBlocks.VOIDBLOCK.getDelegate().value(),
                        SMPBlocks.VOIDPRESENCEBLOCK.getDelegate().value())) {
            return;
        }

        if ((livingEntity instanceof Player player) && ProvenanceDataHandler.getAbilities(player).stream()
                .anyMatch(ability -> ability instanceof VoidPresenceAbility)) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 2 * 20, 0));
            return;
        }

        VoidPresenceTracker tracker;

        if (!livingEntity.hasData(YkorioAttachments.VOID_PRESENCE_TRACKER)) {
            tracker = new VoidPresenceTracker();
            livingEntity.setData(YkorioAttachments.VOID_PRESENCE_TRACKER, tracker);
        } else {
            tracker = livingEntity.getData(YkorioAttachments.VOID_PRESENCE_TRACKER);
        }

        boolean insideNow = isEntityIntersectingTargetBlock(livingEntity, level,
                SMPBlocks.VOIDBLOCK.getDelegate().value(), SMPBlocks.VOIDPRESENCEBLOCK.getDelegate().value());

        boolean wasInsideBeforeUpdate = tracker.isInside();

        tracker.updatePrevious();
        tracker.setInside(insideNow);

        if (tracker.isInside()) {
            DEFERRED.add(() -> {
                if (livingEntity.isAlive()) {
                    livingEntity.hurt(level.damageSources().sweetBerryBush(), 1f);
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 1));
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 0));
                }
            });

            BlockPos fogBlock = getFirstIntersectingBlockPos(livingEntity, level,
                    SMPBlocks.VOIDBLOCK.getDelegate().value(),
                    SMPBlocks.VOIDPRESENCEBLOCK.getDelegate().value());
            if (fogBlock != null) {
                tracker.setLastInsidePos(fogBlock);
            } else {
                tracker.setLastInsidePos(livingEntity.blockPosition());
            }
        }

        if (wasInsideBeforeUpdate && !insideNow) {
            if (isVoidPresenceActive(level)) {
                BlockPos lastPos = tracker.getLastInsidePos();
                if (lastPos != null) {
                    livingEntity.teleportTo(lastPos.getX() + 0.5, lastPos.getY()+.5, lastPos.getZ() + 0.5);
                    livingEntity.setDeltaMovement(Vec3.ZERO);
                    livingEntity.fallDistance = 0;
                    insideNow = true;
                    tracker.setInside(true);
                    tracker.updatePrevious();

                    livingEntity.level().playSound(null, livingEntity.getOnPos(), YkorioSounds.TRYTOLEAVESMOKE.value(),
                            SoundSource.PLAYERS, 1f, 1f);
                }
            } else {
                livingEntity.removeData(YkorioAttachments.VOID_PRESENCE_TRACKER);
            }
        }
    }

    public static BlockPos getFirstIntersectingBlockPos(LivingEntity target, Level level, Block... targetBlocks) {
        AABB box = target.getBoundingBox();
        for (BlockPos pos : BlockPos.betweenClosed(
                BlockPos.containing(box.minX, box.minY, box.minZ),
                BlockPos.containing(box.maxX, box.maxY, box.maxZ))) {
            BlockState state = level.getBlockState(pos);
            for (Block block : targetBlocks) {
                if (state.is(block)) {
                    return pos.immutable(); // return the fog block's position
                }
            }
        }
        return null;
    }

    private static boolean isVoidPresenceActive(Level level) {
        if (!(level instanceof ServerLevel serverLevel))
            return false;
        for (Player player : serverLevel.players()) {
            if (player.hasData(YkorioAttachments.PRESENCE_NOTICED)) {
                Instant timeStarted = player.getData(YkorioAttachments.PRESENCE_NOTICED);
                if (Instant.now().isBefore(timeStarted.plusMillis(VoidPresenceAbility.LengthinMillis))) {
                    return true;
                } else {
                    player.removeData(YkorioAttachments.PRESENCE_NOTICED);
                }
            }
        }
        return false;
    }

    public static boolean isEntityIntersectingTargetBlock(LivingEntity target, Level level, Block... targetBlocks) {
        AABB box = target.getBoundingBox();
        Iterable<BlockPos> overlappingPositions = BlockPos.betweenClosed(
                BlockPos.containing(box.minX, box.minY, box.minZ),
                BlockPos.containing(box.maxX, box.maxY, box.maxZ));

        for (BlockPos pos : overlappingPositions) {
            BlockState state = level.getBlockState(pos);
            for (Block block : targetBlocks) {
                if (state.is(block)) {
                    return true;
                }
            }
        }
        return false;
    }
}

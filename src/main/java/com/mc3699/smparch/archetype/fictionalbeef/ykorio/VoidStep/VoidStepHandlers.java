package com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidStep;

import net.mc3699.provenance.ProvenanceDataHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickEmpty;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.Optional;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.archetype.fictionalbeef.flamek1ng_.Brimstone.BrimstonePayload;
import com.mc3699.smparch.archetype.fictionalbeef.scapune.DecieversGrace.DecieversGhost.DecieversGhostEntity;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.YkorioAttachments;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.YkorioSounds;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidDomain.VoidDomainHandlers;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidStep.VoidEcho.VoidEchoEntity;
import com.mc3699.smparch.registry.SMPParticles;
import com.mojang.logging.LogUtils;

@EventBusSubscriber(modid = SMPArch.MODID)
public class VoidStepHandlers {

    public static double getRadius(Player player) {
        return Math.clamp((double) (26f * (VoidDomainHandlers.getPlayerLightScaled(player))), 1, 1500);
    }

    @SubscribeEvent
    public static void registerPackets(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
                VoidStepPayload.TYPE,
                VoidStepPayload.CODEC,
                (payload, context) -> {
                    context.enqueueWork(() -> {
                        net.minecraft.world.entity.player.Player player = context.player();
                        executeVoidStepServer(player);
                    });
                });
    }

    public static void executeVoidStepServer(Player player) {
        if (!player.hasData(YkorioAttachments.VOID_STEP) || player.level().isClientSide) {
            return;
        }

        double radius = getRadius(player);

        Vec3 eyePosition = player.getEyePosition(1.0F);
        Vec3 lookDirection = player.getViewVector(1.0F);
        Vec3 endPoint = eyePosition.add(lookDirection.scale(radius));

        BlockHitResult hitResult = player.level().clip(new ClipContext(
                eyePosition,
                endPoint,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.SOURCE_ONLY,
                player));

        BlockPos targetPos;

        if (hitResult.getType() == HitResult.Type.BLOCK) {
            targetPos = hitResult.getBlockPos().relative(hitResult.getDirection());
        } else {
            targetPos = new BlockPos(
                    Mth.floor(endPoint.x),
                    Mth.floor(endPoint.y),
                    Mth.floor(endPoint.z));
        }

        BlockPos safeStandingPos = findGroundBelow(player.level(), targetPos,radius);

        if (safeStandingPos != null) {
            if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {

                VoidEchoEntity ghost = VoidEchoEntity.CreateClone(serverPlayer);
                ghost.setData(YkorioAttachments.ECHO_UUID.get(), Optional.of(player.getUUID()));

                player.level().addFreshEntity(ghost);

                serverPlayer.teleportTo(
                        serverPlayer.serverLevel(), // Keep them in the current dimension
                        safeStandingPos.getX() + 0.5,
                        safeStandingPos.getY(),
                        safeStandingPos.getZ() + 0.5,
                        serverPlayer.getYRot(),
                        serverPlayer.getXRot());

                player.level().playSound(null, safeStandingPos,
                        YkorioSounds.MAJIK.value(),
                        SoundSource.PLAYERS, 0.5f, 1.2f);

                if (player.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(SMPParticles.MAJIK.get(), player.getX(),
                            safeStandingPos.getY(), player.getZ(),
                            5, 0.3, -0.3, 0.6, 0.01);
                } else {
                    player.level().addParticle(SMPParticles.MAJIK.get(), player.getX(),
                            safeStandingPos.getY(), player.getZ(),
                            0.0D, 0.6D, 0.0D);
                }
            }

            player.removeData(YkorioAttachments.VOID_STEP);
            player.syncData(YkorioAttachments.VOID_STEP);
        }
    }

    @SubscribeEvent
    public static void onHitBlock(PlayerInteractEvent.RightClickBlock event) {

        executeVoidStepServer(event.getEntity());
    }

    @SubscribeEvent
    public static void onHitItem(PlayerInteractEvent.RightClickItem event) {
        executeVoidStepServer(event.getEntity());
    }

    @SubscribeEvent
    public static void onHitEmptyAir(PlayerInteractEvent.RightClickEmpty event) {
        if (event.getEntity().hasData(YkorioAttachments.VOID_STEP)) {
            PacketDistributor.sendToServer(new VoidStepPayload());
        }
    }

    private static BlockPos findGroundBelow(Level level, BlockPos startPos, double maxDistance) {
        BlockPos.MutableBlockPos pos = startPos.mutable();
        double maxDistanceSq = maxDistance * maxDistance;

        for (int i = 0; i < maxDistance; i++) {
            if (level.isOutsideBuildHeight(pos)) {
                break;
            }

            // Check if we've exceeded the max distance from start
            if (pos.distSqr(startPos) > maxDistanceSq) {
                break;
            }

            BlockPos below = pos.below();
            VoxelShape floorShape = level.getBlockState(below).getCollisionShape(level, below);

            if (!floorShape.isEmpty()) {
                double floorTop = floorShape.max(Direction.Axis.Y);

                boolean floorIsSafe = !level.getFluidState(below).is(FluidTags.LAVA)
                        && floorTop <= 1.0;

                if (floorIsSafe) {
                    double standY = below.getY() + floorTop;
                    AABB playerBox = EntityType.PLAYER.getSpawnAABB(
                            pos.getX() + 0.5, standY, pos.getZ() + 0.5);

                    if (level.noCollision(playerBox)) {
                        return pos.immutable();
                    }
                }
            }

            pos.move(Direction.DOWN);
        }
        return null;
    }
}

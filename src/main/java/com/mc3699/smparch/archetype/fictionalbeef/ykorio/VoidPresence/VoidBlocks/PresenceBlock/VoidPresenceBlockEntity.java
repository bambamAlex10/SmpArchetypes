package com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidPresence.VoidBlocks.PresenceBlock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidPresenceAbility;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidPresence.VoidPresenceHandlers;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidPresence.VoidBlocks.PresenceBlock.Registration.VoidBlock;
import com.mc3699.smparch.registry.SMPBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

public class VoidPresenceBlockEntity extends BlockEntity {
    private int expandTimer = 0;
    private static final int MAX_DISTANCE = 13;

    public static final double X_SCALE = 1.5;
    public static final double Y_SCALE = 0.7;
    public static final double Z_SCALE = 1.5;

    public VoidPresenceBlockEntity(BlockPos pos, BlockState state) {
        super(SMPBlocks.VOID_PRESENCE_BE.get(), pos, state);
    }
private void updateBottomBlocks() {
        if (level == null || level.isClientSide) return;

        double radius = getCurrentBoundaryRadius();
        int rx = (int) Math.ceil(radius * X_SCALE);
        int rz = (int) Math.ceil(radius * Z_SCALE);

        for (int dx = -rx; dx <= rx; dx++) {
            for (int dz = -rz; dz <= rz; dz++) {
                double horizDistSq = (dx / X_SCALE) * (dx / X_SCALE) + (dz / Z_SCALE) * (dz / Z_SCALE);
                if (horizDistSq > radius * radius) continue; 

                int maxDy = (int) Math.floor(Math.sqrt(radius * radius - horizDistSq) * Y_SCALE);
                int minY = worldPosition.getY() - maxDy;
                int maxY = worldPosition.getY() + maxDy;

                int worldX = worldPosition.getX() + dx;
                int worldZ = worldPosition.getZ() + dz;

                BlockPos bottomPos = null;
                for (int y = maxY; y >= minY; y--) {
                    BlockPos checkPos = new BlockPos(worldX, y, worldZ);
                    BlockState state = level.getBlockState(checkPos);
                    if (state.is(SMPBlocks.VOIDBLOCK.get())) {
                        bottomPos = checkPos;
                        break; 
                    }
                }

                for (int y = maxY; y >= minY; y--) {
                    BlockPos pos = new BlockPos(worldX, y, worldZ);
                    BlockState state = level.getBlockState(pos);
                    if (state.is(SMPBlocks.VOIDBLOCK.get())) {
                        boolean shouldBeBottom = pos.equals(bottomPos);
                        if (state.getValue(VoidBlock.DIRECTIONS[0]) != shouldBeBottom) {
                            level.setBlock(pos, state.setValue(VoidBlock.DIRECTIONS[0], shouldBeBottom),
                                    Block.UPDATE_CLIENTS | Block.UPDATE_NEIGHBORS);
                        }
                    }
                }
            }
        }
    }
    public void tick() {
        if (this.level == null || this.level.isClientSide)
            return;

        expandTimer++;

        if (expandTimer % 10 == 0 && !this.level.isClientSide) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(),
                    this.getBlockState(), Block.UPDATE_CLIENTS);
        }

        if (expandTimer % 2 == 0) {
            int currentRadius = expandTimer / 2;
            if (currentRadius <= MAX_DISTANCE) {
                int rx = (int) Math.ceil(currentRadius * X_SCALE);
                int ry = (int) Math.ceil(currentRadius * Y_SCALE);
                int rz = (int) Math.ceil(currentRadius * Z_SCALE);

                for (int x = -rx; x <= rx; x++) {
                    for (int y = -ry; y <= ry; y++) {
                        for (int z = -rz; z <= rz; z++) {
                            double dx = x / X_SCALE;
                            double dy = y / Y_SCALE;
                            double dz = z / Z_SCALE;
                            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

                            if (dist <= currentRadius && dist > currentRadius - 2.0) {
                                BlockPos targetPos = this.worldPosition.offset(x, y, z);
                                BlockState targetState = this.level.getBlockState(targetPos);

                                if (targetState.isAir() || (targetState.canBeReplaced() && !targetState.getFluidState().is(FluidTags.WATER) )) {
                                    this.level.setBlock(targetPos, SMPBlocks.VOIDBLOCK.get().defaultBlockState(), 3);

                                    // Schedule removal (outside‑in collapse)
                                    int totalExpansionTime = (((int) VoidPresenceAbility.LengthinMillis)/1000) * 20;
                                    int baseTicksUntilCollapse = totalExpansionTime - expandTimer;
                                    int outwardStagger = (MAX_DISTANCE - currentRadius) * 10;
                                    int finalDelay = baseTicksUntilCollapse + outwardStagger;

                                    this.level.scheduleTick(targetPos, SMPBlocks.VOIDBLOCK.get(),
                                            Math.max(1, finalDelay));
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Core dissipation
        int maxLifespan = (MAX_DISTANCE * 20) + (MAX_DISTANCE * 10) + 10;
        if (expandTimer >= maxLifespan) {
            this.level.removeBlock(this.worldPosition, false);
        }
    }

    private static final Map<ResourceKey<Level>, Map<ChunkPos, Set<BlockPos>>> CORES_BY_CHUNK = new HashMap<>();

    private void registerToChunk() {
        if (level == null || level.isClientSide)
            return;
        ChunkPos chunk = new ChunkPos(worldPosition);
        CORES_BY_CHUNK.computeIfAbsent(level.dimension(), d -> new HashMap<>())
                .computeIfAbsent(chunk, c -> new HashSet<>())
                .add(worldPosition);
    }

    private void unregisterFromChunk() {
        if (level == null || level.isClientSide)
            return;
        Map<ChunkPos, Set<BlockPos>> dimMap = CORES_BY_CHUNK.get(level.dimension());
        if (dimMap != null) {
            Set<BlockPos> set = dimMap.get(new ChunkPos(worldPosition));
            if (set != null) {
                set.remove(worldPosition);
                if (set.isEmpty())
                    dimMap.remove(new ChunkPos(worldPosition));
            }
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        registerToChunk();
    }

    @Override
    public void setRemoved() {
        unregisterFromChunk();
        super.setRemoved();
    }

    public double getCurrentBoundaryRadius() {
        int totalExpansionTime = MAX_DISTANCE * 20;
        double radius;
        if (expandTimer <= totalExpansionTime) {
            radius = Math.min(expandTimer / 20.0, MAX_DISTANCE);
        } else {
            int layersLost = (expandTimer - totalExpansionTime) / 10;
            radius = Math.max(0, MAX_DISTANCE - layersLost);
        }
        return Math.max(0.5, radius); // keep a tiny cage until core removal
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putInt("expandTimer", this.expandTimer);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        super.handleUpdateTag(tag, registries);
        this.expandTimer = tag.getInt("expandTimer");
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider registries) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) {
            this.handleUpdateTag(tag, registries);
        }
    }
}
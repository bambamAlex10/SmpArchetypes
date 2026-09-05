package com.mc3699.smparch.archetype.fictionalbeef.scapune.DecieversGrace;

import java.util.Optional;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.archetype.fictionalbeef.scapune.ScapuneAttachments;
import com.mc3699.smparch.archetype.fictionalbeef.scapune.DecieversGrace.DecieversGhost.DecieversGhostEntity;
import com.mc3699.smparch.registry.SMPEntities;
import com.mojang.logging.LogUtils;

import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity.Server;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;

public class DecieversGraceAbility extends BaseAbility {
    public static int GhostDuration = 20 * 10;

    @Override
    public boolean canExecute(ServerPlayer arg0) {
        return !arg0.hasData(ScapuneAttachments.GHOST_UUID);
    }

    @Override
    public Component getName() {
        return Component.literal("Decievers Grace");
    }

    @Override
    public float getUseCost() {
        return 1.0f;
    }

    @Override
    public int getCooldown() {
        return 30 * 20;
    }

    @Override
    public void execute(ServerPlayer player) {
        DecieversGhostEntity ghost = DecieversGhostEntity.CreateClone(player);
        ghost.setData(ScapuneAttachments.GHOST_UUID.get(), Optional.of(player.getUUID()));

        player.level().addFreshEntity(ghost);

        player.setData(ScapuneAttachments.GHOST_UUID.get(), Optional.of(ghost.getUUID()));
        player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 10 * 20, 0, true, false), player);

        player.getChunkTrackingView().forEach(chunkPosLong -> {

            ChunkPos pos = new ChunkPos(chunkPosLong.toLong());

            if (!player.serverLevel().isPositionEntityTicking(pos.getWorldPosition())) {
                return;
            }

            double minX = pos.getMinBlockX();
            double minZ = pos.getMinBlockZ();
            double maxX = pos.getMaxBlockX();
            double maxZ = pos.getMaxBlockZ();

            AABB chunkVolume = new AABB(minX, player.serverLevel().getMinBuildHeight(), minZ, maxX,
                    player.serverLevel().getMaxBuildHeight(), maxZ);

            for (Mob mob : player.serverLevel().getEntitiesOfClass(Mob.class, chunkVolume)) {

                if (!(mob instanceof Enemy) || mob.getTarget() != player) {
                    continue;
                }
                
                mob.setTarget(ghost);

            }

        });

    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.fromNamespaceAndPath(SMPArch.MODID, "textures/ability_icon/scapune/decieversgrace.png");
    }
}

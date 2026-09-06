package com.mc3699.smparch.archetype.fictionalbeef.ykorio;

import java.time.Instant;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.registry.SMPAttachments;
import com.mc3699.smparch.registry.SMPBlocks;

import net.mc3699.provenance.ability.foundation.AmbientAbility;
import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class VoidPresenceAbility extends BaseAbility {
    public static final long LengthinMillis = 60000;

    @Override
    public boolean canExecute(ServerPlayer player) {
        return true;
    }

    @Override
    public Component getName() {
        return Component.literal("Void Presence");
    }

    @Override
    public float getUseCost() {
        return 1.5f;
    }

    @Override
    public int getCooldown() {
        return 10 * 60 * 30;
    }

    public void execute(ServerPlayer player) {
        super.execute(player);

        Level level = player.level();

        Vec3 motion = player.getDeltaMovement();
        double futureX = player.getX() + (motion.x * 3.5);
        double futureY = player.getY() + (motion.y * 3.5);
        double futureZ = player.getZ() + (motion.z * 3.5);

        BlockPos pos = BlockPos.containing(futureX, futureY + 1, futureZ);

        if (level.isEmptyBlock(pos)
                || (level.getBlockState(pos).canBeReplaced() && !level.getFluidState(pos).is(FluidTags.WATER))) {
            level.setBlockAndUpdate(pos, SMPBlocks.VOIDPRESENCEBLOCK.get().defaultBlockState());
        }

        player.setData(YkorioAttachments.PRESENCE_NOTICED, Instant.now());

        player.level().playSound(null, player.getOnPos(), YkorioSounds.SMOKESTARTS.value(),
                        SoundSource.PLAYERS, 1f, 1f);
    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.fromNamespaceAndPath(SMPArch.MODID, "textures/ability_icon/ykorio/voidpresence.png");
    }
}
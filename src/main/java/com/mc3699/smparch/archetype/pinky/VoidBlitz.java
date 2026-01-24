package com.mc3699.smparch.archetype.pinky;

import com.mc3699.smparch.SMPArch;
import com.tom.cpm.shared.config.Player;

import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class VoidBlitz extends BaseAbility {

    private boolean justTeleported = false;

    @Override
    public boolean canExecute(ServerPlayer Player) {
        return true;
    }

    @Override
    public Component getName() {
        return Component.literal("Void Blitz");
    }

    @Override
    public void execute(ServerPlayer player) {
        double range = 20.0;
        Vec3 startPos = player.getEyePosition();
        Vec3 lookVec = player.getViewVector(1.0F);
        Vec3 endPos = startPos.add(lookVec.scale(range));

        BlockHitResult hitResult = player.level().clip(new ClipContext(
                startPos,
                endPos,
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE,
                player));

        Vec3 teleportDestination;

        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos hitPos = hitResult.getBlockPos();
            Direction side = hitResult.getDirection();
            teleportDestination = new Vec3(
                    hitPos.getX() + 0.5 + side.getStepX(),
                    hitPos.getY() + side.getStepY(),
                    hitPos.getZ() + 0.5 + side.getStepZ());
        } else {
            teleportDestination = endPos;
        }

        player.teleportTo(teleportDestination.x, teleportDestination.y, teleportDestination.z);
        justTeleported = true;
        super.backgroundTick(player);
    }

    @Override
    public void backgroundTick(ServerPlayer player) {
        super.backgroundTick(player);

        if((!player.onGround()) && justTeleported) {
            player.fallDistance = 0;
        } else if (justTeleported && player.onGround()) { 
            justTeleported = false;
        }
    }

    @Override
    public float getUseCost() {
        return 1.5f;
    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.fromNamespaceAndPath(SMPArch.MODID, "textures/ability_icon/void_blitz.png");
    }
}

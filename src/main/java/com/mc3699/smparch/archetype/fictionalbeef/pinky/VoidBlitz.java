package com.mc3699.smparch.archetype.fictionalbeef.pinky;

import java.util.ArrayList;
import java.util.Collections;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.registry.SMPSounds;

import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.mc3699.provenance.util.ProvScheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class VoidBlitz extends BaseAbility {
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
        double range = 10.0;

        Vec3 startPos = player.getEyePosition();
        Vec3 lookVec = player.getViewVector(1.0F);
        Vec3 endPos = startPos.add(lookVec.scale(range));

        Level serverLevel = player.level();

        BlockHitResult hitResult = serverLevel.clip(new ClipContext(
                startPos,
                endPos,
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE,
                player));

        Vec3 teleportDestination;
        boolean hitBlock = false;

        if (hitResult.getType() == HitResult.Type.BLOCK) {
            hitBlock = true;
            BlockPos hitPos = hitResult.getBlockPos();
            Direction side = hitResult.getDirection();
            teleportDestination = new Vec3(
                    hitPos.getX() + 0.5 + side.getStepX(),
                    hitPos.getY() + side.getStepY(),
                    hitPos.getZ() + 0.5 + side.getStepZ());
        } else {
            teleportDestination = endPos;
        }

        BlockPos targetBlock = BlockPos.containing(teleportDestination);

        if (hitBlock) {
            BlockState state = serverLevel.getBlockState(targetBlock);

            int randomness = 2;

            Iterable<BlockPos> Hits = BlockPos.betweenClosed(
                    targetBlock.offset(-randomness, -randomness, -randomness),
                    targetBlock.offset(randomness, randomness, randomness)

            );
            ArrayList<BlockPos> Possible = new ArrayList<>();

            for (BlockPos Block : Hits) {

                if (state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)) {
                    if (state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER) {
                        Block = Block.below();
                    }
                }

                state = serverLevel.getBlockState(Block);

                if (state.isAir() && serverLevel.getBlockState(Block.above()).isAir()
                        && serverLevel.getBlockState(Block.below()).isSolid()) {
                    Possible.add(Block.immutable());
                }
            }

            Collections.shuffle(Possible);
            BlockPos Chosen = Possible.get(0);



            player.teleportTo(Chosen.getX(), Chosen.getY(), Chosen.getZ());

            ProvScheduler.schedule(5, () -> serverLevel.playSound(null, Chosen, PinkySounds.VOIDBLITZ.value(),
                    SoundSource.PLAYERS, 0.2f, 1.2f));
        } else {
            player.teleportTo(teleportDestination.x, teleportDestination.y, teleportDestination.z);

            ProvScheduler.schedule(5, () -> serverLevel.playSound(null, targetBlock, PinkySounds.VOIDBLITZ.value(),
                    SoundSource.PLAYERS, 0.2f, 1.5f));
        }
    }

    @Override
    public int getCooldown() {
        return 20 * 2;
    }

    @Override
    public float getUseCost() {
        return 1f;
    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.fromNamespaceAndPath(SMPArch.MODID, "textures/ability_icon/void_blitz.png");
    }
}

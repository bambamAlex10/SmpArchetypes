package com.mc3699.smparch.archetype.tbs.pencil_html;

import net.mc3699.provenance.ability.foundation.ToggleAbility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class EndStepAbility extends ToggleAbility {

    private Vec3 teleportPosition;

    @Override
    public int getCooldown() {
        return 10;
    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.fromNamespaceAndPath(
                "smparch",
                "textures/ability_icon/end_step.png"
        );
    }

    @Override
    public float getUseCost() {
        return 0.07f;
    }

    @Override
    public Component getName() {
        return Component.literal("End Step");
    }

    @Override
    public boolean canExecute(ServerPlayer player) {
        return true;
    }

    @Override
    protected void onToggle(ServerPlayer player, boolean enabled) {
        AttributeInstance moveSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        ResourceLocation modifierId =
                ResourceLocation.fromNamespaceAndPath("smparch", "end_step_slow");

        if (!enabled && moveSpeed != null) {
            moveSpeed.removeModifier(modifierId);
            teleportPosition = null;
        }
    }

    @Override
    public void tick(ServerPlayer player) {
        AttributeInstance moveSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        ResourceLocation modifierId =
                ResourceLocation.fromNamespaceAndPath("smparch", "end_step_slow");

        if (moveSpeed != null && moveSpeed.getModifier(modifierId) == null) {
            moveSpeed.addTransientModifier(
                    new AttributeModifier(
                            modifierId,
                            -0.035,
                            AttributeModifier.Operation.ADD_VALUE
                    )
            );
        }

        spawnTeleportParticles(player, player.serverLevel());

        if (player.isShiftKeyDown() && teleportPosition != null) {
            teleportPlayer(player, teleportPosition);
        }

        super.tick(player);
    }

    @Override
    public void backgroundTick(ServerPlayer player) {
        if (player.isInWaterOrRain()) {
            player.hurt(player.damageSources().drown(), 2.0f);
        }
    }

    private void teleportPlayer(ServerPlayer player, Vec3 position) {
        ServerLevel level = player.serverLevel();

        player.teleportTo(position.x, position.y, position.z);

        disableSelf(player);

        level.playSound(
                null,
                position.x,
                position.y,
                position.z,
                SoundEvents.ENDERMAN_TELEPORT,
                SoundSource.PLAYERS,
                1f,
                0.7f
        );
    }

    private void spawnTeleportParticles(ServerPlayer player, Level level) {
        float radius = 0.75f;
        int particleCount = 28;
        float maxRange = 16f;

        Vec3 eyePosition = player.getEyePosition();
        Vec3 lookAngle = player.getLookAngle();
        Vec3 endPosition = eyePosition.add(lookAngle.scale(maxRange));
        Vec3 targetPosition = null;

        BlockHitResult result = level.clip(
                new ClipContext(
                        eyePosition,
                        endPosition,
                        ClipContext.Block.COLLIDER,
                        ClipContext.Fluid.NONE,
                        player
                )
        );

        if (result.getType() != HitResult.Type.MISS) {
            Vec3 hitPos = result.getLocation();

            if (result.getDirection() == Direction.UP) {
                targetPosition = hitPos;
            } else {
                BlockPos hitBlock = result.getBlockPos();
                BlockPos above = hitBlock.above();
                BlockState state = level.getBlockState(above);
                Block block = state.getBlock();

                Vec3 top = new Vec3(
                        hitBlock.getX() + 0.5,
                        hitBlock.getY() + 1.0,
                        hitBlock.getZ() + 0.5
                );

                if (eyePosition.distanceTo(top) <= maxRange && block == Blocks.AIR) {
                    targetPosition = top;
                }
            }
        } else {
            BlockHitResult down = level.clip(
                    new ClipContext(
                            endPosition,
                            endPosition.add(0, -256, 0),
                            ClipContext.Block.COLLIDER,
                            ClipContext.Fluid.NONE,
                            player
                    )
            );

            if (down.getType() != HitResult.Type.MISS &&
                    down.getDirection() == Direction.UP) {
                targetPosition = down.getLocation();
            }
        }

        if (targetPosition != null) {
            teleportPosition = targetPosition;

            for (int i = 0; i < particleCount; i++) {
                double angle = (i / (double) particleCount) * Math.PI * 2;
                double x = radius * Math.cos(angle);
                double z = radius * Math.sin(angle);

                player.serverLevel().sendParticles(
                        new DustParticleOptions(
                                new Vector3f(0.7f, 0.3f, 0.9f),
                                0.7f
                        ),
                        targetPosition.x + x,
                        targetPosition.y + 0.1,
                        targetPosition.z + z,
                        1,
                        0, 0, 0,
                        0
                );
            }
        }
    }
}

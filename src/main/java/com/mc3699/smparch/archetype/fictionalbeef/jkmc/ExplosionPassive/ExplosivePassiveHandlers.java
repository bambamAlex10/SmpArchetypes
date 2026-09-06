package com.mc3699.smparch.archetype.fictionalbeef.jkmc.ExplosionPassive;

import java.util.HashSet;
import java.util.Set;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.archetype.fictionalbeef.alivealex.BloodofCovenant.BloodofCovenantHandlers;
import com.mc3699.smparch.archetype.fictionalbeef.flamek1ng_.BrimstonePassive;
import com.mc3699.smparch.archetype.fictionalbeef.jkmc.JkmcAttachments;
import com.mc3699.smparch.archetype.fictionalbeef.jkmc.RezePassive;
import com.mc3699.smparch.archetype.fictionalbeef.scapune.DecieversWill.DecieversWillHandlers;
import com.mojang.logging.LogUtils;

import net.mc3699.provenance.ProvenanceDataHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber(modid = SMPArch.MODID)
public class ExplosivePassiveHandlers {

    public static final float oneMeter = .01f;
    public static final float onHitCharge = oneMeter * .5f;
    public static final float onBreakCharge = oneMeter * .5f;
    public static final float maxCharges = 35f;

    private static final Set<BlockPos> BROKEN = new HashSet<>();
    private static final Set<BlockPos> EMPTYDROP = new HashSet<>();

    public static void handleEffects(ServerLevel level, Vec3 position) {
        level.sendParticles(
                ParticleTypes.EXPLOSION,
                position.x + 0.5,
                position.y + 0.5,
                position.z + 0.5,
                1,
                0.0, 0.0, 0.0,
                0.0);
        level.playSound(
                null,
                new BlockPos((int) position.x, (int) position.y, (int) position.z),
                SoundEvents.GENERIC_EXPLODE.value(),
                SoundSource.BLOCKS,
                0.5f,
                1.0f);

    }

    public static void handleBlock(ServerPlayer player, BlockPos relPos) {
        ServerLevel level = player.serverLevel();
        if (BROKEN.contains(relPos))
            return;
        if (player.level().getBlockState(relPos).isAir())
            return;

        if (!
        DecieversWillHandlers.safeBreak(player, relPos, player.level().getBlockState(relPos))) return;

        handleEffects(level, relPos.getCenter());

        BROKEN.add(relPos);
        EMPTYDROP.add(relPos);


        player.gameMode.destroyBlock(relPos);
    }

    public static void clearBlocks() {
        BROKEN.clear();
        EMPTYDROP.clear();
    }

    @SubscribeEvent
    public static void onBlockBroke(BlockEvent.BreakEvent event) {
        Player breaker = event.getPlayer();
        if (!(breaker instanceof ServerPlayer player))
            return;

        BlockPos position = event.getPos();
        if (BROKEN.contains(position))
            return;

        if (ProvenanceDataHandler.getAmbientAbilities(breaker).stream()
                .noneMatch(ability -> ability instanceof RezePassive)) {
            return;
        } else if (breaker.isCrouching()) {
            return;
        }

        ServerLevel level = player.serverLevel();

        try {

            handleBlock(player, position);

            float hardness = event.getState().getBlock().defaultDestroyTime();
            float digSpeed = player.getDigSpeed(event.getState(), event.getPos());

            float breakTimeTicks;
            if (digSpeed > 0.001f && hardness >= 0) {
                breakTimeTicks = (hardness * 1.5f * 20f) / digSpeed;
            } else {
                breakTimeTicks = Float.MAX_VALUE;
            }

            float addedCharge = onBreakCharge * (breakTimeTicks / 10);

            if (!breaker.hasData(JkmcAttachments.JKMC_CHARGE)) {
                breaker.setData(JkmcAttachments.JKMC_CHARGE, addedCharge);
            } else {
                float CurrentCharge = breaker.getData(JkmcAttachments.JKMC_CHARGE);
                breaker.setData(JkmcAttachments.JKMC_CHARGE,
                        Math.clamp(CurrentCharge + addedCharge, 0.0f, maxCharges * oneMeter));
            }

            breaker.syncData(JkmcAttachments.JKMC_CHARGE);

            for (Direction dir : Direction.Plane.HORIZONTAL) {
                BlockPos relPos = position.relative(dir);
                handleBlock(player, relPos);

            }

            for (Direction dir : Direction.Plane.VERTICAL) {
                BlockPos relPos = position.relative(dir);
                handleBlock(player, relPos);
            }

        } finally {
            clearBlocks();
            event.setCanceled(true);
        }

    }

    @SubscribeEvent
    public static void LivingDamageEvent(LivingDamageEvent.Pre event) {
        Entity entity = event.getSource().getEntity();

        if ((entity instanceof ServerPlayer hitter)) {
            if (ProvenanceDataHandler.getAmbientAbilities(hitter).stream()
                    .noneMatch(ability -> ability instanceof RezePassive)) {
                return;
            }

            if (!BloodofCovenantHandlers.isholdingWeapon(hitter)) {
                handleEffects(hitter.serverLevel(), event.getEntity().position());
                event.setNewDamage(event.getNewDamage() * 5);
            }

            float newCharge = onHitCharge * hitter.getAttackStrengthScale(1.0f);

            if (!hitter.hasData(JkmcAttachments.JKMC_CHARGE)) {
                hitter.setData(JkmcAttachments.JKMC_CHARGE, newCharge);
            } else {
                float CurrentCharge = hitter.getData(JkmcAttachments.JKMC_CHARGE);
                hitter.setData(JkmcAttachments.JKMC_CHARGE,
                        Math.clamp(CurrentCharge + newCharge, 0.0f, maxCharges * oneMeter));
            }

            hitter.syncData(JkmcAttachments.JKMC_CHARGE);
        } else if (event.getEntity() instanceof ServerPlayer gettingHit) {
            if ((ProvenanceDataHandler.getAmbientAbilities(gettingHit).stream()
                    .noneMatch(ability -> ability instanceof RezePassive))) {
                return;

            }
            if (!event.getSource().is(net.minecraft.tags.DamageTypeTags.IS_FIRE)
                    && !event.getSource().is(net.minecraft.tags.DamageTypeTags.IS_EXPLOSION)) {
                return;
            }


            event.setNewDamage(0f);
        }
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof ItemEntity item))
            return;

        BlockPos entityPos = item.blockPosition();

        if (EMPTYDROP.remove(entityPos)) {
            event.setCanceled(true);
        }

    }

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();

        if (ProvenanceDataHandler.getAmbientAbilities(player).stream()
                .noneMatch(ability -> ability instanceof RezePassive))
            return;

        if (player.isCrouching()) {
            float newSpeed = event.getOriginalSpeed() * 0.8f;
            event.setNewSpeed(newSpeed);
        }
    }
}

package com.mc3699.smparch.archetype.tbs.miku;

import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.mc3699.provenance.util.ProvScheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AidFromBelowAbility extends BaseAbility {
    @Override
    public float getUseCost() {
        return 6;
    }

    @Override
    public int getCooldown() { return 180*40; }

    @Override
    public Component getName() {
        return Component.literal("Aid From Below");
    }

    private static final double SUMMON_RANGE = 3.0;
    private static final int DESPAWN_TICKS = 1200;
    public static final Logger LOGGER = LogManager.getLogger();

    private Warden summonedWarden = null;
    private int wardenLifetime = 0;
    private boolean startedDigging = false;

    private static BlockPos findSafeSpawnPosition(Level level, Vec3 center) {
        BlockPos centerPos = BlockPos.containing(center);

        for (int attempts = 0; attempts < 20; attempts++) {
            double angle = level.random.nextDouble() * Math.PI * 2;
            double distance = 2.0 + level.random.nextDouble() * (SUMMON_RANGE - 2.0);

            int x = (int) (center.x + Math.cos(angle) * distance);
            int z = (int) (center.z + Math.sin(angle) * distance);

            for (int y = centerPos.getY() + 5; y >= centerPos.getY() - 10; y--) {
                BlockPos pos = new BlockPos(x, y, z);
                BlockPos posAbove = pos.above();
                BlockPos posAbove2 = pos.above(2);

                if (!level.getBlockState(pos).isAir() &&
                        level.getBlockState(posAbove).isAir() &&
                        level.getBlockState(posAbove2).isAir()) {
                    return posAbove;
                }
            }
        }
        return centerPos;
    }

    @Override
    public void execute(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        Vec3 playerPos = player.position();
        BlockPos spawnPos = findSafeSpawnPosition(level, playerPos);

        if (spawnPos != null) {
            Warden warden = EntityType.WARDEN.create(level);
            if (warden != null) {
                warden.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5,
                        level.random.nextFloat() * 360F, 0.0F);

                warden.finalizeSpawn(level,
                        level.getCurrentDifficultyAt(warden.blockPosition()),
                        MobSpawnType.TRIGGERED, null);

                warden.getAttribute(Attributes.MAX_HEALTH).setBaseValue(75.0);
                warden.setHealth(75.0f);
                warden.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(15.0);

                MobEffectInstance weaknessEffect = new MobEffectInstance(MobEffects.WEAKNESS, -1, 1, false, false, true);

                warden.addEffect(weaknessEffect);
                warden.setPersistenceRequired();
                warden.getPersistentData().putBoolean("NoDrops", true);
                warden.setCustomName(Component.literal("Summoned Warden"));
                warden.setCustomNameVisible(true);

                level.addFreshEntity(warden);

                level.playSound(null, spawnPos, SoundEvents.WARDEN_EMERGE, SoundSource.PLAYERS, 1.0F, 0.8F);
                level.playSound(null, spawnPos, SoundEvents.SCULK_CATALYST_BLOOM, SoundSource.PLAYERS, 0.8F, 1.2F);

                summonedWarden = warden;
                wardenLifetime = 0;
            }
        }
    }

    @Override
    public void backgroundTick(ServerPlayer serverPlayer) {
        ServerLevel level = serverPlayer.serverLevel();
        if (level.isClientSide || summonedWarden == null || !summonedWarden.isAlive()) return;
        wardenLifetime++;
        if (wardenLifetime < DESPAWN_TICKS) return;
        if (startedDigging) return;
        //LOGGER.debug("he gone");
        startedDigging = true;
        summonedWarden.playSound(SoundEvents.WARDEN_DIG, 5.0F, 1.0F);
        summonedWarden.setPose(Pose.DIGGING);
        summonedWarden.removeFreeWill();
        ProvScheduler.schedule(6*20, () -> {
            summonedWarden.remove(Entity.RemovalReason.DISCARDED);
            summonedWarden = null;
            wardenLifetime = 0;
            startedDigging = false;
        });
    }

    @Override
    public boolean canExecute(ServerPlayer serverPlayer) {
        return true;
    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.fromNamespaceAndPath("minecraft","textures/block/sculk_shrieker_can_summon_inner_top.png");
    }
}
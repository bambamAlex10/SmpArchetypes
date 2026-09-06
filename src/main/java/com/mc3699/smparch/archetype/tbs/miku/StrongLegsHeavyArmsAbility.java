package com.mc3699.smparch.archetype.tbs.miku;

import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.mc3699.provenance.util.ProvScheduler;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import static net.minecraft.world.entity.projectile.windcharge.AbstractWindCharge.EXPLOSION_DAMAGE_CALCULATOR;

public class StrongLegsHeavyArmsAbility extends BaseAbility {
    boolean inAir = false;
    int flyTime = 0;
    static final float radius = 8F;

    @Override
    public float getUseCost() {
        return 1.5f;
    }

    @Override
    public int getCooldown() { return 10*40; }

    @Override
    public Component getName() {
        return Component.literal("Strong Legs, Heavy Arms");
    }

    @Override
    public void execute(ServerPlayer player) {
        super.execute(player);

        Vec3 lookDir = player.getLookAngle().normalize();

        boolean usedOnGround = player.onGround() || player.fallDistance < 3;

        if(usedOnGround) {
            player.addDeltaMovement(new Vec3(lookDir.x, 0.88, lookDir.z));
            player.hurtMarked = true;
        }
        
        spawnParticles(player, ParticleTypes.GLOW, 30, Vec3.ZERO, 3);

        ProvScheduler.schedule(usedOnGround ? 10 : 0, () -> {
            player.setDeltaMovement(new Vec3(0, -5, 0));
            inAir = true;
            player.hurtMarked = true;
        });

    }

    void slam(ServerPlayer plr) {
        Level level = plr.serverLevel();
        inAir = false;
        flyTime = 0;

        BlockParticleOption particle = new BlockParticleOption(ParticleTypes.DUST_PILLAR, Blocks.SCULK.defaultBlockState());

        level.explode(
            plr, null, EXPLOSION_DAMAGE_CALCULATOR,
            plr.getX(), plr.getY(), plr.getZ(),
            radius, false,
            Level.ExplosionInteraction.NONE,
            false,
            null,
            null,
            null
        );

        level.playSound(null, plr.getX(), plr.getY(), plr.getZ(), Holder.direct(SoundEvents.WARDEN_ATTACK_IMPACT), SoundSource.PLAYERS, 1f, 1f);

        spawnParticles(plr, particle, 100, new Vec3(1, 0 , 1), 0.5f);
        spawnParticles(plr, ParticleTypes.SCULK_CHARGE_POP, 30, Vec3.ZERO, 0.2f);

        AABB box = plr.getBoundingBox().inflate(radius);
        for(LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, box))
            if(!(e == plr)) {
                e.hurt(e.damageSources().playerAttack(plr), 5f);
                e.knockback(2f, plr.getX() - e.getX(), plr.getZ() - e.getZ());
            }
    }

    void spawnParticles(ServerPlayer player, ParticleOptions options, int count, Vec3 offset, float speed)
    {
        ServerLevel level = player.serverLevel();
        level.sendParticles(
                options,
                player.getX(), player.getY(), player.getZ(),
                count,
                offset.x, offset.y, offset.z,
                speed
        );
    }

    @Override
    public void backgroundTick(ServerPlayer serverPlayer) {
        super.backgroundTick(serverPlayer);

        if(inAir) {
            flyTime++;
            serverPlayer.fallDistance = 0;
            if(flyTime > 0 && serverPlayer.onGround()) slam(serverPlayer);
        }
    }
    
    @Override
    public boolean canExecute(ServerPlayer player) {
        return true;
    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.fromNamespaceAndPath("minecraft","textures/block/sculk_sensor_tendril_inactive.png");
    }
}
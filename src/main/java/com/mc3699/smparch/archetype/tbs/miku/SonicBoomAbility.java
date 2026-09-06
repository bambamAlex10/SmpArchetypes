package com.mc3699.smparch.archetype.tbs.miku;

import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.mc3699.provenance.util.ProvScheduler;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;

public class SonicBoomAbility extends BaseAbility {
    @Override
    public float getUseCost() {
        return 1.5f;
    }

    @Override
    public int getCooldown() { return 10*40; }

    @Override
    public Component getName() {
        return Component.literal("Sonic Boom");
    }

    @Override
    public void execute(ServerPlayer player) {
        super.execute(player);

        LivingEntity entity = getLookedAtEntity(player);

        Level level = player.serverLevel();
        player.serverLevel().sendParticles(ParticleTypes.SCULK_CHARGE_POP,player.getX(),player.getY(),player.getZ(),20,Vec3.ZERO.x,Vec3.ZERO.y,Vec3.ZERO.z,0.1f);

        level.playSound(null, player.getBlockPosBelowThatAffectsMyMovement().above(1), SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.PLAYERS);

        ProvScheduler.schedule(20, () -> sonicBoom(player.serverLevel(), player.getEyePosition(), entity == null ? getLookedAtBlock(player).getLocation() : entity.getEyePosition(), player));
    }

    private static LivingEntity getLookedAtEntity(ServerPlayer player) {
        HitResult hitResult = ProjectileUtil.getHitResultOnViewVector(
                player,
                e -> e.isPickable() && e instanceof LivingEntity,
                20f
        );
        if (hitResult instanceof EntityHitResult ehr) return (LivingEntity) ehr.getEntity();
        return null;
    }


    private static BlockHitResult getLookedAtBlock(ServerPlayer player) {
        Vec3 start = player.getEyePosition();
        Vec3 dir = player.getLookAngle();
        Vec3 end = start.add(dir.scale(20f));
        
        return player.serverLevel().clip(
                new ClipContext(
                start,
                end,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                player
        ));
    }

     private static void sonicBoom(ServerLevel level, Vec3 start, Vec3 end, LivingEntity owner) {
        Vec3 distance = end.subtract(start);
        Vec3 dir = distance.normalize();
        int steps = Mth.floor(distance.length()) + 7;

        for (int i = 1; i < steps; ++i) {
            Vec3 p = start.add(dir.scale(i));
            level.sendParticles(ParticleTypes.SONIC_BOOM, p.x, p.y, p.z, 1, 0, 0, 0, 0);
        }

        level.playSound(null, start.x, start.y, start.z, SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 3.0F, 1.5F);

        AABB box = new AABB(
            end, start
        );
        box.inflate(0.5f);

        for(LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, box))
            if(!(e == owner)) {
                if (e.hurt(level.damageSources().sonicBoom(owner), 7.0F)) {

                    double y  = 0.5 * (1.0 - e.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                    double xz = 2.5 * (1.0 - e.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));

                    e.push(dir.x() * xz, dir.y() * y, dir.z() * xz);
                }
            }

    }


    @Override
    public boolean canExecute(ServerPlayer serverPlayer) {
        return true;
    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.fromNamespaceAndPath("minecraft","textures/block/sculk_catalyst_top_bloom.png");
    }
}

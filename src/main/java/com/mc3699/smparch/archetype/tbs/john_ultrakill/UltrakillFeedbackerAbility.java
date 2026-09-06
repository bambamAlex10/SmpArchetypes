package com.mc3699.smparch.archetype.tbs.john_ultrakill;

import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class UltrakillFeedbackerAbility extends BaseAbility {
    @Override
    public float getUseCost() {
        return 0;
    }

    @Override
    public Component getName() {
        return Component.literal("Vector Redirection System");
    }

    @Override
    public boolean canExecute(ServerPlayer serverPlayer) {
        return true;
    }

    @Override
    public void execute(ServerPlayer player) {
        super.execute(player);

        ServerLevel level = player.serverLevel();
        AABB box = new AABB(player.getBlockPosBelowThatAffectsMyMovement().above(2)).inflate(4);

        List<Projectile> projectiles = level.getEntitiesOfClass(Projectile.class, box);
        Vec3 look = player.getLookAngle().normalize();

        for (Projectile p : projectiles) {
            if (p.getOwner() != player && !p.onGround()) {
                p.hurtMarked = true;
                //p.setPos(player.position().add(0,1,0));
                p.setDeltaMovement(0,0,0);
                p.playSound(SoundEvents.ANVIL_PLACE);
                level.addParticle(ParticleTypes.ELECTRIC_SPARK, p.getX(), p.getY(), p.getZ(), 0,0,0);
                p.setDeltaMovement(look.scale(2));
                p.hurtMarked = true;
                p.setOwner(player);
            }
        }
    }
}
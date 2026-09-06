package com.mc3699.smparch.archetype.fictionalbeef.jkmc;

import java.util.Random;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.archetype.fictionalbeef.jkmc.ExplosionPassive.ExplosivePassiveHandlers;
import com.mc3699.smparch.archetype.fictionalbeef.jkmc.RezeLargeHit.RezeLargeHitHandlers;
import com.mc3699.smparch.registry.SMPSounds;
import com.mc3699.smparch.registry.SMPParticles;

import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ExplosivePropulsion extends BaseAbility {

    private static final float dashVelocityPaidMax = 4.3f;
    private static final float dashVelocityPaidMin = 3.7f;
    private static final float dashVelocityDebt = 4.5f;

    private static final float MAX_DASH_VELOCITY = 8.0f; // hard cap per dash, blocks/tick-ish — tune
    private static final float MAX_STORED_MOMENTUM = 12.0f; // hard cap on the attachment value — tune
    private static final float MAX_DASH_HEALTH_COST = 6.0f; 

    private static float sanitize(float v, float max) {
        if (Float.isNaN(v) || Float.isInfinite(v))
            return 0f;
        return Mth.clamp(v, 0f, max);
    }

    private static Random randomPitch = new Random();

    @Override
    public boolean canExecute(ServerPlayer Player) {
        return Player.hasData(JkmcAttachments.JKMC_CHARGE)
                && (Player.getData(JkmcAttachments.JKMC_CHARGE) > ExplosivePassiveHandlers.oneMeter * .1f);
    }

    @Override
    public int getCooldown() {
        return 0;
    }

    @Override
    public Component getName() {
        return Component.literal("ExplosionPropulse");
    }

    @Override
    public float getUseCost() {
        return 0.0f;
    }

    public void execute(ServerPlayer player) {
        super.execute(player);
        Vec3 lookDir = player.getLookAngle();

        ServerLevel level = player.serverLevel();
        player.setData(JkmcAttachments.JKMC_LARGEHIT, false);

        ExplosivePassiveHandlers.handleEffects(level, player.getPosition(0.0f));

        float storedMomentum = sanitize(
                player.hasData(JkmcAttachments.JKMC_DYINGMOMENTUM)
                        ? player.getData(JkmcAttachments.JKMC_DYINGMOMENTUM)
                        : 0f,
                MAX_STORED_MOMENTUM);

        float dashVelocity = storedMomentum;
        boolean paid = player.hasData(JkmcAttachments.JKMC_CHARGE)
                && player.getData(JkmcAttachments.JKMC_CHARGE) > ExplosivePassiveHandlers.oneMeter * 7f;

        if (paid) {
            dashVelocity += randomPitch.nextFloat(dashVelocityPaidMax - dashVelocityPaidMin) + dashVelocityPaidMin;
        } else {
            dashVelocity += dashVelocityDebt;
        }

        dashVelocity = sanitize(dashVelocity, MAX_DASH_VELOCITY);
        Vec3 dashMotion = lookDir.scale(dashVelocity);

        RezeLargeHitHandlers.applyVelocity(player, dashVelocity, dashMotion, null);

        if (paid) {
            RezeLargeHitHandlers.takeCharge(player, ExplosivePassiveHandlers.oneMeter * 7f);
        } else {
            RezeLargeHitHandlers.takeCharge(player, ExplosivePassiveHandlers.oneMeter * 0.1f);
            float healthCost = Math.min(1.5f * (dashVelocity / dashVelocityDebt), MAX_DASH_HEALTH_COST);
            player.setHealth(player.getHealth() - healthCost);
        }

        player.setData(JkmcAttachments.JKMC_DYINGMOMENTUM,
                sanitize(dashVelocity, MAX_STORED_MOMENTUM));
    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.fromNamespaceAndPath(SMPArch.MODID, "textures/ability_icon/jkmc/bigblastlaunch.png");
    }

}

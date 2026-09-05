package com.mc3699.smparch.archetype.fictionalbeef.jkmc.RezeLargeHit;

import javax.annotation.Nullable;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.archetype.fictionalbeef.jkmc.JkmcAttachments;
import com.mc3699.smparch.archetype.fictionalbeef.jkmc.ExplosionPassive.ExplosivePassiveHandlers;
import com.mojang.logging.LogUtils;

import net.mc3699.provenance.ProvenanceDataHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.event.entity.player.SweepAttackEvent;

@EventBusSubscriber(modid = SMPArch.MODID)
public class RezeLargeHitHandlers {
    public static final float oneMeter = ExplosivePassiveHandlers.oneMeter;

    public static void takeCharge(Player myself, float meter) {
        float currentCharge = myself.getData(JkmcAttachments.JKMC_CHARGE);
        myself.setData(JkmcAttachments.JKMC_CHARGE, currentCharge - (meter * currentCharge));
        myself.syncData(JkmcAttachments.JKMC_CHARGE);
    }

    public static void applyVelocity(LivingEntity victim,float Strength, Vec3 Direction, @Nullable Float noFallDamage) {

        if (victim instanceof ServerPlayer supervictim) {
            victim.knockback(Strength*.5, -Direction.x, -Direction.z);
            victim.setDeltaMovement(victim.getDeltaMovement().x, Direction.y,
                    victim.getDeltaMovement().z);

            supervictim.connection.send(new ClientboundSetEntityMotionPacket(supervictim));
            
        } else {
            victim.setDeltaMovement(Direction.x,Direction.y,Direction.z);
            victim.hasImpulse = true;

        }

        if (noFallDamage != null) {
            victim.fallDistance = noFallDamage;
        }
    }

    public static void standingHit(ServerPlayer hitter, LivingEntity victim) {
        Level level = hitter.level();

        BlockPos feetPos = victim.blockPosition();
        BlockPos under1 = feetPos.below();
        BlockPos under2 = under1.below();

        BlockState state1 = level.getBlockState(under1);
        BlockState state2 = level.getBlockState(under2);

        if (!state1.isAir()) {
            hitter.gameMode.destroyBlock(under1);
        }
        if (!state2.isAir()) {
            hitter.gameMode.destroyBlock(under2);
        }

        applyVelocity(victim, -2, new Vec3(victim.getDeltaMovement().x, -2.0, victim.getDeltaMovement().z),
                Math.max(victim.fallDistance, 4.0f));
        takeCharge(hitter, 3.0f);

        hitter.removeData(JkmcAttachments.JKMC_LARGEHIT);
    }

    public static void sprintingHit(ServerPlayer hitter, LivingEntity victim) {
        float yaw = hitter.getYRot();
        double rad = Math.toRadians(yaw);
        Vec3 horizontal = new Vec3(-Math.sin(rad), 0, Math.cos(rad)).normalize().scale(3);

        if (horizontal.lengthSqr() > 0.0001) {
            horizontal = horizontal.normalize().scale(1);
        }

        applyVelocity(victim, 2, new Vec3(horizontal.x, 0.3, horizontal.z), null);
        takeCharge(hitter, 2.0f);

        hitter.removeData(JkmcAttachments.JKMC_LARGEHIT);

    }

    public static void criticalHit(ServerPlayer hitter, LivingEntity victim) {
        float yaw = hitter.getYRot();
        double rad = Math.toRadians(yaw);
        Vec3 horizontal = new Vec3(-Math.sin(rad), 0, Math.cos(rad)).normalize().scale(1);

        if (horizontal.lengthSqr() > 0.0001) {
            horizontal = horizontal.normalize().scale(2);
        }

        applyVelocity(hitter, 2, new Vec3(horizontal.x, 1.1, horizontal.z), 0.0f);
        applyVelocity(victim, 5, new Vec3(horizontal.x, 1.0, horizontal.z), null);

        hitter.setData(JkmcAttachments.JKMC_LARGEHIT,false);
        takeCharge(hitter, 5.0f);
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingIncomingDamageEvent event) { // Detect Sprint Hits
        Entity entity = event.getSource().getEntity();

        if (!(entity instanceof ServerPlayer hitter)) {
            return;
        }

        if (ProvenanceDataHandler.getAbilities(hitter).stream()
                .noneMatch(ability -> ability instanceof RezeLargeHitAbility))
            return;
        if (!hitter.hasData(JkmcAttachments.JKMC_LARGEHIT) || !hitter.getData(JkmcAttachments.JKMC_LARGEHIT)) {
            return;
        }

        LivingEntity victim = event.getEntity();

        if (hitter.onGround() && !hitter.isSprinting()) {
            standingHit(hitter, victim);
        } else if (hitter.isSprinting()) {
            sprintingHit(hitter, victim);
        } else {
            criticalHit(hitter, victim);
        }

    }

    @SubscribeEvent
public static void onLivingFall(LivingFallEvent event) {
    if (!(event.getEntity() instanceof ServerPlayer hitter)) {
        return;
    }
    if (ProvenanceDataHandler.getAbilities(hitter).stream().noneMatch(ability -> ability instanceof RezeLargeHitAbility)) {
        return;
    }

    if (hitter.hasData(JkmcAttachments.JKMC_LARGEHIT) && hitter.getData(JkmcAttachments.JKMC_LARGEHIT) == false) {
        event.setDistance(0);
        hitter.removeData(JkmcAttachments.JKMC_LARGEHIT);
    }
}


}

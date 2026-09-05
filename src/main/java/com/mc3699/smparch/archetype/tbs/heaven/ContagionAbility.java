package com.mc3699.smparch.archetype.tbs.heaven;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.registry.SMPSounds;
import net.mc3699.provenance.ProvenanceDataHandler;
import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

import static net.minecraft.world.entity.projectile.ProjectileUtil.getEntityHitResult;

public class ContagionAbility extends BaseAbility {

    @Override
    public float getUseCost() {
        return 2f;
    }

    @Override
    public Component getName() {
        return Component.literal("Contagion").withStyle(ChatFormatting.GREEN);
    }

    @Override
    public int getCooldown() {
        return 40;
    }

    @Override
    public void execute(ServerPlayer player) {
        super.execute(player);

        double range = 30.0;
        Vec3 eyePos = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        Vec3 endPos = eyePos.add(look.scale(range));
        ServerLevel serverLevel = player.serverLevel();
        AABB searchArea = player.getBoundingBox().expandTowards(look.scale(range)).inflate(1.0);

        EntityHitResult entityHit = null;
        double closestDistance = range;

        for (Entity entity : serverLevel.getEntities(player, searchArea,
                e -> e != player && e.isAlive() && e.isPickable() && !e.isSpectator())) {

            AABB entityBox = entity.getBoundingBox().inflate(entity.getPickRadius());
            Optional<Vec3> hitVec = entityBox.clip(eyePos, endPos);

            if (hitVec.isPresent()) {
                double distance = eyePos.distanceTo(hitVec.get());

                if (distance < closestDistance) {
                    closestDistance = distance;
                    entityHit = new EntityHitResult(entity, hitVec.get());
                }
            }
        }

        if (entityHit != null) {
            Entity targetEntity = entityHit.getEntity();
            if (targetEntity instanceof LivingEntity livingTarget) {
                livingTarget.addEffect(new MobEffectInstance(
                        MobEffects.POISON, 12*20, 0));
                player.playNotifySound(
                        SMPSounds.QUICKHACK.value(),
                        SoundSource.PLAYERS,
                        1.0f,
                        1.0f
                );
            }
        }
        if (entityHit == null) {
            ProvenanceDataHandler.changeAP(player, 2f);
        }
    }
    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.fromNamespaceAndPath("minecraft", "textures/mob_effect/poison.png");
    }

    @Override
    public boolean canExecute(ServerPlayer serverPlayer) {
        return true;
    }
}

package com.mc3699.smparch.archetype.fictionalbeef.bigmanrake.Accelerando;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.annotation.Nullable;

import com.ibm.icu.impl.Pair;
import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.archetype.fictionalbeef.bigmanrake.BigManRakeAttachments;
import com.mc3699.smparch.archetype.fictionalbeef.bigmanrake.BigManRakeSounds;
import com.mc3699.smparch.archetype.fictionalbeef.bigmanrake.Concerto.ConcertoHandlers;
import com.mc3699.smparch.archetype.fictionalbeef.flamek1ng_.FlameK1ng_Attachments;
import com.mc3699.smparch.archetype.fictionalbeef.jkmc.RezeLargeHit.RezeLargeHitHandlers;
import com.mc3699.smparch.registry.SMPSounds;
import com.mc3699.smparch.util.DelayedTaskHandler;
import com.mc3699.smparch.util.EntityHitbox.EntityHitbox;
import com.mojang.logging.LogUtils;
import com.mc3699.smparch.registry.SMPEntities;
import com.mc3699.smparch.registry.SMPParticles;

import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class AccelerandoAbility extends BaseAbility {

    private static final double DASH_VELOCITY = 2d;
    private static Random randomPitch = new Random();
    private static boolean inCreative = false;
    private static float invalidSpeed = .1f;

    @Override
    public boolean canExecute(ServerPlayer Player) {
        return true;
    }

    @Override
    public int getCooldown() {
        if (inCreative) {
            return 0;
        }

        return 2 * 20;
    }

    @Override
    public Component getName() {
        return Component.literal("Accelerando");
    }

    @Override
    public float getUseCost() {
        if (inCreative) {
            return 0.0f;
        }

        return 3f;
    }

    public static boolean firstHit(LivingEntity player, LivingEntity gotHit) {

        Optional<Reference<MobEffect>> insanityEffect = BuiltInRegistries.MOB_EFFECT
                .getHolder(ConcertoHandlers.insanityResource);

        if (insanityEffect.isPresent() && !gotHit.hasEffect(insanityEffect.get().getDelegate())) {
            LogUtils.getLogger().debug("what");
            return false;
        }

        List<LivingEntity> lastHits = new ArrayList<>();
        lastHits.add(player);

                    if (player instanceof Player dealer) {
        AccelerandoAbility.entityHit(8, gotHit, dealer);
                    };
        lastHits.add(gotHit);
        AccelerandoAbility.nextHit(lastHits, 7);

        return true;
    }

    public static void heal(int scaleUp, LivingEntity victim, Player dealer) {

        ResourceLocation insanity = ConcertoHandlers.insanityResource;
        Optional<Reference<MobEffect>> insanityEffect = BuiltInRegistries.MOB_EFFECT
                .getHolder(insanity);

        insanityEffect.ifPresentOrElse(effect -> {
            if (!victim.hasEffect(effect)) {
                victim
                        .addEffect(new MobEffectInstance(effect.getDelegate(), (int) ((50 * 20)/scaleUp), (int) 0, true, false));
                dealer.removeData(BigManRakeAttachments.VIBRATO);
                return;
            }
        }, () -> {
        });
    }

    public static void entityHit(int Scale, LivingEntity hit, Player damager, EntityHitbox causie) {


        // hit.hurt(new DamageSource(hit.level().damageSources().generic().typeHolder(), causie, hit), 1.0f);
        heal(Scale,hit,damager);
    }

    public static void entityHit(int Scale, LivingEntity hit, Player damager) {
        heal(Scale,hit,damager);
        // hit.hurt(new DamageSource(hit.level().damageSources().generic().typeHolder(), hit), 1.0f);
    }

    public static boolean nextHit(List<LivingEntity> lastHits, final int SizeGiven) {
        LivingEntity startingFrom = lastHits.getLast();

        AABB newHitbox = new AABB(startingFrom.blockPosition()).inflate(SizeGiven);

        double furthestDistance = 1000;
        // double maximumDistance = ;

        LivingEntity furthestEntity = null;
        LivingEntity lastEntity = lastHits.getLast();

        for (LivingEntity loop : lastEntity.level().getEntitiesOfClass(LivingEntity.class, newHitbox)) {
            if (lastHits.contains(loop)) {
                // LogUtils.getLogger().debug("Skipped something i already seen");
                continue;
            }


            double distance = loop.position().subtract(startingFrom.position()).length();

            // LogUtils.getLogger().debug("max distance is "+maximumDistance+" at "+(SizeGiven+1)+" squared");

            if (furthestDistance > (distance) //&& distance <= maximumDistance
        ) {
                furthestDistance = distance;
                furthestEntity = loop;
            } else {
                // LogUtils.getLogger().debug(loop.getName().getString() + " is "+ distance+ " units away?");


            }
        }

        if (furthestEntity == null || !(furthestEntity instanceof LivingEntity)) {
                LogUtils.getLogger().debug("invalid?");
            if (SizeGiven <= 0) {
                return true;
            } else {
                return false;
            }
        }

        EntityHitbox newHit = new EntityHitbox(SMPEntities.ENTITYHITBOX.get(), lastEntity.level(), lastHits,
                furthestEntity.position(), (Pair<EntityHitbox, LivingEntity> givenPair) -> {

                    if (lastHits.getFirst() instanceof Player dealer) {
                    entityHit(SizeGiven, givenPair.second, dealer, givenPair.first);
                    }

                    lastHits.add(givenPair.second);

                    // LogUtils.getLogger().debug("hit + 1! at "+givenPair.second.position());
                    givenPair.first.discard();

                    return nextHit(lastHits, (SizeGiven - 1));
                },
                (EntityHitbox self) -> {
                    
        if (self.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(SMPParticles.NOTES.get(), self.getX(), self.getY()+1.0f, self.getZ(), 40, 0.3, -0.3, 0.3, 0.01);
        }else {
            self.level().addParticle(SMPParticles.NOTES.get(), self.getX(), self.getY()+1.0, self.getZ(), 0.0D, 0.1D, 0.0D);
        }
        return false;
                });

        newHit.setPos(lastEntity.position());
        newHit.speed = .8f;
        newHit.lifeTime =  (int) Math.ceil(furthestEntity.distanceTo(newHit)/newHit.speed) ;
        newHit.ready();
        lastEntity.level().addFreshEntity(newHit);
        
        // LogUtils.getLogger().debug("will live for "+(newHit.lifeTime/20)+" seconds");

        return true;
    }

    public void execute(ServerPlayer player) {
        super.execute(player);

        Vec3 currentPosition = player.getPosition(1f);

        if (player.gameMode.isCreative()) {
            inCreative = true;
        } else {
            inCreative = false;
        }

        Vec3 lookDir = player.getLookAngle();

        Vec3 dashMotion = lookDir.scale(DASH_VELOCITY);
        // player.setDeltaMovement(dashMotion);

        Level level = player.level();

        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(SMPParticles.NOTES.get(), player.getX(), player.getY() + 1.0, player.getZ(), 10,
                    0.3, 0.3, 0.3, 0.05);
        } else {
            level.addParticle(SMPParticles.NOTES.get(), player.getX(), player.getY() + 1.0, player.getZ(), 0.0D, 0.1D,
                    0.0D);
        }

        if (!player.hasData(BigManRakeAttachments.ACCELEARNDO) || !player.getData(BigManRakeAttachments.ACCELEARNDO)) {

            player.level().playSound(null, player.getBlockPosBelowThatAffectsMyMovement(),
                    BigManRakeSounds.ACC1.value(),
                    SoundSource.PLAYERS, 0.2f, randomPitch.nextFloat() * (1.2f - .8f) + 1f);
            player.setData(BigManRakeAttachments.ACCELEARNDO, true);
        } else {
            player.level().playSound(null, player.getBlockPosBelowThatAffectsMyMovement(),
                    BigManRakeSounds.ACC2.value(),
                    SoundSource.PLAYERS, 0.2f, randomPitch.nextFloat() * (1.2f - .8f) + 1f);
            player.setData(BigManRakeAttachments.ACCELEARNDO, false);
        }

        player.setDeltaMovement(dashMotion);
        player.hasImpulse = true;

        int ticks = (int) (Math.log(invalidSpeed / DASH_VELOCITY) / Math.log(.91f));

        player.setData(BigManRakeAttachments.HITBOXHIT, true);

        DelayedTaskHandler.scheduleDelayed(ticks, () -> {
            Vec3 newPosition = player.getPosition(1f);

            AABB hitbox = new AABB(currentPosition, newPosition);

            if (player.hasData(BigManRakeAttachments.HITBOXHIT)) {
                player.removeData(BigManRakeAttachments.HITBOXHIT);
            } else {
                return;
            }

            for (LivingEntity wasHit : player.level().getEntitiesOfClass(LivingEntity.class, hitbox)) {
                if (wasHit.equals(player)) {
                    continue;
                }

                if (firstHit(player, wasHit)) {
                    // LogUtils.getLogger().debug("yes it hit");
                    break;
                }

                // LogUtils.getLogger().debug("what the hell");
            }
            ;
        });
    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.fromNamespaceAndPath(SMPArch.MODID, "textures/ability_icon/bigmanrake/accelerando.png");
    }

}

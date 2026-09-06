package com.mc3699.smparch.archetype.fictionalbeef.flamek1ng_.WaxenPinion;

import java.util.Random;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.archetype.fictionalbeef.bigmanrake.BigManRakeAttachments;
import com.mc3699.smparch.archetype.fictionalbeef.bigmanrake.BigManRakeSounds;
import com.mc3699.smparch.archetype.fictionalbeef.flamek1ng_.FlameK1ng_Attachments;
import com.mc3699.smparch.archetype.fictionalbeef.flamek1ng_.FlameK1ng_Sounds;
import com.mc3699.smparch.archetype.fictionalbeef.flamek1ng_.Brimstone.BrimstoneHandlers;
import com.mc3699.smparch.registry.SMPParticles;
import com.mc3699.smparch.util.DelayedTaskHandler;
import com.mojang.logging.LogUtils;

import net.mc3699.provenance.ability.foundation.AmbientAbility;
import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class WaxenPinionActive extends BaseAbility {
    private static Random randomPitch = new Random();
    private static float invalidSpeed = .1f;

    // @Override
    // public void tick(ServerPlayer player) {
    // }

    @Override
    public boolean canExecute(ServerPlayer player) {
        return true;
    }

    @Override
    public Component getName() {
        return Component.literal("Waxen Pinion");
    }

    @Override
    public float getUseCost() {
        return 3f;

    }

    @Override
    public int getCooldown() {
        return 2 * 20;
    }

    public static float getBaseDamageWithoutContext(ItemStack stack) {
        ItemAttributeModifiers modifiers = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
        if (modifiers == null) {
            return 1.0f;
        }

        for (ItemAttributeModifiers.Entry entry : modifiers.modifiers()) {
            if (!entry.attribute().equals(Attributes.ATTACK_DAMAGE)) {
                continue;
            }

            return (float) entry.modifier().amount();
        }

        return 1.0f;
    }

    public static void hitEntity(LivingEntity wasHit, LivingEntity attacking) {

        float startingDamage = getBaseDamageWithoutContext(attacking.getMainHandItem());

        if (attacking.getData(FlameK1ng_Attachments.HASHEADS)) {
            if (wasHit.hasEffect(MobEffects.FIRE_RESISTANCE)) {
                wasHit.removeEffect(MobEffects.FIRE_RESISTANCE);
            }
            startingDamage *= 2f;
            wasHit.setRemainingFireTicks(((int) startingDamage) * 20);
        }

        wasHit.hurt(new DamageSource(attacking.level().damageSources().inFire().typeHolder(), attacking),
                startingDamage);
    }

    public void execute(ServerPlayer player) {
        super.execute(player);

        Vec3 currentPosition = player.getPosition(1f);

        Vec3 lookDir = player.getLookAngle();


        if (!player.hasData(FlameK1ng_Attachments.FIREDASH) || !player.getData(FlameK1ng_Attachments.FIREDASH)) {
            player.level().playSound(null, player.getBlockPosBelowThatAffectsMyMovement(),
                    FlameK1ng_Sounds.FIREDASH1.value(),
                    SoundSource.PLAYERS, 0.2f, randomPitch.nextFloat() * (1.2f - .8f) + 1f);
            player.setData(FlameK1ng_Attachments.FIREDASH, true);
        } else {
            player.level().playSound(null, player.getBlockPosBelowThatAffectsMyMovement(),
                    FlameK1ng_Sounds.FIREDASH2.value(),
                    SoundSource.PLAYERS, 0.2f, randomPitch.nextFloat() * (1.2f - .8f) + 1f);
            player.setData(FlameK1ng_Attachments.FIREDASH, false);
        }

        boolean heads = BrimstoneHandlers.flipCoin(player);
        Level level = player.level();

        double DASH_VELOCITY = 2.5d;
        float divisible = .55f;

        if (!player.onGround() && lookDir.y > .2) {
            DASH_VELOCITY = 2d;
            divisible = .91f;
        }

        Vec3 dashMotion = lookDir.scale(DASH_VELOCITY);
        player.setDeltaMovement(dashMotion);
        player.hurtMarked = true;
        player.fallDistance = 0;
        // player.setInvulnerable(true);

        int ticks = (int) (Math.log(invalidSpeed / DASH_VELOCITY) / Math.log(divisible));

        player.setData(FlameK1ng_Attachments.HASHEADS, heads);
        player.setData(FlameK1ng_Attachments.HITBOXHIT, true);

        DelayedTaskHandler.scheduleDelayed(ticks, () -> {
            // player.setInvulnerable(false);
            // player.hurtMarked = true;
            // player.fallDistance = 0;

            Vec3 newPosition = player.getPosition(1f);

            AABB hitbox = new AABB(currentPosition, newPosition).inflate(4, 4, 4);

            if (player.hasData(FlameK1ng_Attachments.HITBOXHIT)) {
                player.removeData(FlameK1ng_Attachments.HITBOXHIT);
            }

            for (LivingEntity wasHit : player.level().getEntitiesOfClass(LivingEntity.class, hitbox)) {
                if (wasHit == player) {
                    return;
                }

                hitEntity(wasHit, player);
            }
            ;

        });
        // if (player.level() instanceof ServerLevel serverLevel) {
        // serverLevel.sendParticles(SMPParticles.NOTES.get(), player.getX(),
        // player.getY() + 1.0, player.getZ(), 10,
        // 0.3, 0.3, 0.3, 0.05);
        // } else {
        // level.addParticle(SMPParticles.NOTES.get(), player.getX(), player.getY() +
        // 1.0, player.getZ(), 0.0D, 0.1D,
        // 0.0D);
        // }

        // if (!player.hasData(BigManRakeAttachments.ACCELEARNDO) ||
        // } else {
        // player.level().playSound(null,
        // player.getBlockPosBelowThatAffectsMyMovement(),
        // BigManRakeSounds.ACC2.value(),
        // SoundSource.PLAYERS, 0.2f, randomPitch.nextFloat() * (1.2f - .8f) + 1f);
        // player.setData(BigManRakeAttachments.ACCELEARNDO, false);
        // }
    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.fromNamespaceAndPath(SMPArch.MODID, "textures/ability_icon/flamek1ng_/waxenpinion.png");
    }

}

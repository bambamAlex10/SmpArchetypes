package com.mc3699.smparch.archetype.tbs.firelight;

import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SimpleExplosionDamageCalculator;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.function.Function;

import static net.minecraft.world.entity.projectile.windcharge.AbstractWindCharge.EXPLOSION_DAMAGE_CALCULATOR;

public class DragonLeapAbility extends BaseAbility {
    @Override
    public float getUseCost() {
        return 1;
    }

    @Override
    public void execute(ServerPlayer player) {
        explode(player.serverLevel(), new Vec3(player.getX(), player.getY(), player.getZ()));
        player.setDeltaMovement(player.getDeltaMovement().multiply(2f,1.5f,2f));
        player.hurtMarked = true;
        super.execute(player);
    }

    @Override
    public Component getName() {
        return Component.literal("Dragon Leap");
    }

    @Override
    public boolean canExecute(ServerPlayer serverPlayer) {
        return serverPlayer.onGround();
    }
    private void explode(ServerLevel serverLevel, Vec3 pos) {
        serverLevel.explode(null, null, EXPLOSION_DAMAGE_CALCULATOR, pos.x(), pos.y(), pos.z(), 2.0F, false, Level.ExplosionInteraction.TRIGGER, ParticleTypes.GUST_EMITTER_SMALL, ParticleTypes.GUST_EMITTER_LARGE, SoundEvents.WIND_CHARGE_BURST);
    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.fromNamespaceAndPath("minecraft","textures/item/dragon_breath.png");
    }
}

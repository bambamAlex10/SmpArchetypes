package com.mc3699.smparch.generic_abilities;

import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

public class FireballAbility extends BaseAbility {


    @Override
    public int getCooldown() {
        return 4*40;
    }

    @Override
    public float getUseCost() {
        return 2.5f;
    }

    @Override
    public Component getName() {
        return Component.literal("Fireball").withStyle(ChatFormatting.GOLD);
    }

    @Override
    public void execute(ServerPlayer player) {
        super.execute(player);
        ServerLevel serverLevel = player.serverLevel();
        Vec3 lookVec = player.getLookAngle();
        Vec3 spawnPos = player.getEyePosition().add(lookVec.scale(1.0f));
        Vec3 velocity = lookVec.scale(1.5f);

        SmallFireball fireball = new SmallFireball(
                serverLevel,
                player,
                velocity
        );
        fireball.setPos(spawnPos);
        fireball.setDeltaMovement(velocity);

        serverLevel.addFreshEntity(fireball);
serverLevel.playSound(null, player.getBlockPosBelowThatAffectsMyMovement().above(1), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1, 1);
    }

    @Override
    public boolean canExecute(ServerPlayer serverPlayer) {
        return true;
    }


    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.fromNamespaceAndPath("minecraft", "textures/item/fire_charge.png");
    }
}

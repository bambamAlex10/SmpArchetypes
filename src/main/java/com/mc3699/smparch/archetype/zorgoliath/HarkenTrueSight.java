package com.mc3699.smparch.archetype.zorgoliath;

import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class HarkenTrueSight extends BaseAbility {

    @Override
    public float getUseCost() {
        return 0.5f;
    }

    @Override
    public Component getName() {
        return Component.literal("True Sight");
    }

    @Override
    public int getCooldown() {
        return 400;
    }

    @Override
    public boolean canExecute(ServerPlayer serverPlayer) {
        return true;
    }

    @Override
    public void execute(ServerPlayer player) {
        super.execute(player);
        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 140));

        AABB searchRange = player.getBoundingBox().inflate(16);
        ServerLevel serverLevel = player.serverLevel();
        List<LivingEntity> livingEntities = serverLevel.getEntitiesOfClass(LivingEntity.class, searchRange);

        livingEntities.forEach(livingEntity -> {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 140));
        });
    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.withDefaultNamespace("textures/mob_effect/glowing.png");
    }
}

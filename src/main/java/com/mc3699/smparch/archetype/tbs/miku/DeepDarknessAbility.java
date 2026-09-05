package com.mc3699.smparch.archetype.tbs.miku;

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

public class DeepDarknessAbility extends BaseAbility {
    @Override
    public float getUseCost() {
        return 1.5f;
    }

    @Override
    public int getCooldown() { return 60*40; }

    @Override
    public Component getName() {
        return Component.literal("Deep Darkness");
    }

    @Override
    public void execute(ServerPlayer player) {
        super.execute(player);
        AABB range = player.getBoundingBox().inflate(10);
        ServerLevel serverLevel = player.serverLevel();

        List<LivingEntity> effectEntities = serverLevel.getEntitiesOfClass(LivingEntity.class, range);

        effectEntities.forEach(entity -> {
            if(!entity.is(player))
            {
                entity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 20*20, 1));
            }
        });
    }

    @Override
    public boolean canExecute(ServerPlayer serverPlayer) {
        return true;
    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.fromNamespaceAndPath("minecraft","textures/mob_effect/darkness.png");
    }
}
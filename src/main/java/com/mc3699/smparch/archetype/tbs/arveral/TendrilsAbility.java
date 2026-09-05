package com.mc3699.smparch.archetype.tbs.arveral;

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

public class TendrilsAbility extends BaseAbility {
    @Override
    public float getUseCost() {
        return 5;
    }

    @Override
    public Component getName() {
        return Component.literal("Tendrils");
    }

    @Override
    public void execute(ServerPlayer player) {

        AABB effectiveArea = player.getBoundingBox().inflate(8);
        ServerLevel serverLevel = player.serverLevel();

        List<LivingEntity> entities = serverLevel.getEntitiesOfClass(LivingEntity.class, effectiveArea);

        entities.forEach(entity -> {
            if(!(entity.is(player)))
            {
                entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 10*20));
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10*20));
            }
        });

        super.execute(player);
    }
        @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.fromNamespaceAndPath("minecraft","textures/mob_effect/darkness.png");
    }

    @Override
    public boolean canExecute(ServerPlayer serverPlayer) {
        return serverPlayer.onGround();
    }

    @Override
    public String getAnimation() {
        return "tendrils";
    }
}

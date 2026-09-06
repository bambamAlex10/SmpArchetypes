package com.mc3699.smparch.archetype.zorgoliath;

import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class HarkenerSilentDash extends BaseAbility {
    @Override
    public float getUseCost() {
        return 0;
    }

    @Override
    public Component getName() {
        return Component.literal("Silent Dash");
    }

    @Override
    public boolean canExecute(ServerPlayer serverPlayer) {
        return true;
    }

    @Override
    public void execute(ServerPlayer player) {
        super.execute(player);
        player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 240));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 240, 1));

        AABB searchRange = player.getBoundingBox().inflate(8);
        ServerLevel serverLevel = player.serverLevel();
        List<LivingEntity> livingEntities = serverLevel.getEntitiesOfClass(LivingEntity.class, searchRange);

        livingEntities.forEach(livingEntity -> {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 140));
        });
    }
}

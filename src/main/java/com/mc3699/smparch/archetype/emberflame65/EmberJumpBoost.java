package com.mc3699.smparch.archetype.emberflame65;

import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class EmberJumpBoost extends BaseAbility {
    @Override
    public float getUseCost() {
        return 1.5f;
    }

    @Override
    public int getCooldown() {
        return 900;
    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.withDefaultNamespace("textures/mob_effect/jump_boost.png");
    }

    @Override
    public Component getName() {
        return Component.literal("Jump Boost");
    }

    @Override
    public boolean canExecute(ServerPlayer serverPlayer) {
        return !EmberAbilityUtils.isSoaked(serverPlayer);
    }

    @Override
    public void execute(ServerPlayer player) {
        super.execute(player);
        player.addEffect(new MobEffectInstance(MobEffects.JUMP, 600, 2));
    }
}

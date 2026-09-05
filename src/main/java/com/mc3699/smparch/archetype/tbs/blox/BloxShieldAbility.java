package com.mc3699.smparch.archetype.tbs.blox;

import net.mc3699.provenance.ability.foundation.ToggleAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class BloxShieldAbility extends ToggleAbility {
    @Override
    public float getUseCost() {
        return 0.025f;
    }

    @Override
    public Component getName() {
        return Component.literal("Shield");
    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.fromNamespaceAndPath("minecraft","textures/mob_effect/absorption.png");
    }

    @Override
    public boolean canExecute(ServerPlayer serverPlayer) {
        return BloxArchetype.checkArmor(serverPlayer);
    }

    @Override
    public void tick(ServerPlayer serverPlayer) {
        super.tick(serverPlayer);
        serverPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 4, 4));
        serverPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 4, 4));
    }
}

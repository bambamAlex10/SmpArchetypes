package com.mc3699.smparch.archetype.teebee;

import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.ArrayList;

public class ReverseAbility extends BaseAbility {
    @Override
    public float getUseCost() {
        return 0;
    }

    @Override
    public Component getName() {
        return Component.literal("Reverse");
    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.fromNamespaceAndPath("smparch", "textures/ability_icon/reversal.png");
    }

    @Override
    public boolean canExecute(ServerPlayer serverPlayer) {
        return serverPlayer.experienceLevel >= 20;
    }

    @Override
    public void execute(ServerPlayer player) {
        System.out.println("executed ability");
        player.giveExperienceLevels(-20);
        new ArrayList<>(player.getActiveEffects()).forEach(mobEffectInstance -> {
            if (!mobEffectInstance.getEffect().value().isBeneficial()
                    || mobEffectInstance.is(MobEffects.SLOW_FALLING)) {
                player.removeEffect(mobEffectInstance.getEffect());
            }
        });
    }
}

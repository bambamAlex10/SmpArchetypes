package com.mc3699.smparch.archetype.teebee;

import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.ArrayList;
import java.util.Collection;

public class AmplifyAbility extends BaseAbility {

    @Override
    public float getUseCost() {
        return 0;
    }

    @Override
    public Component getName() {
        return Component.literal("Amplify");
    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.fromNamespaceAndPath("smparch", "textures/ability_icon/amplify.png");
    }

    @Override
    public boolean canExecute(ServerPlayer serverPlayer) {
        return serverPlayer.experienceLevel >= 20;
    }

    @Override
    public void execute(ServerPlayer player) {
        player.giveExperienceLevels(-20);
        new ArrayList<>(player.getActiveEffects()).forEach(mobEffectInstance -> {
            if (mobEffectInstance.getEffect().value().isBeneficial()) {
                MobEffectInstance extendedEffect = new MobEffectInstance(
                        mobEffectInstance.getEffect(), mobEffectInstance.getDuration() + 600,
                        mobEffectInstance.getAmplifier(), mobEffectInstance.isAmbient(),
                        mobEffectInstance.isVisible(), mobEffectInstance.showIcon()
                );
                player.addEffect(extendedEffect);
            }
        });
    }
}

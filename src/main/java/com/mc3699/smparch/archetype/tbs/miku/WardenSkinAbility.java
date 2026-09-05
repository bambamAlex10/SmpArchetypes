package com.mc3699.smparch.archetype.tbs.miku;

import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class WardenSkinAbility extends BaseAbility {
    @Override
    public float getUseCost() {
        return 1;
    }

    @Override
    public int getCooldown() { return 10*40; }

    @Override
    public Component getName() {
        return Component.literal("Warden Skin");
    }

    @Override
    public void execute(ServerPlayer player) {
        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 30*20, 0));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 10*20, 0));
    }

    @Override
    public boolean canExecute(ServerPlayer player) {
        return true;
    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.fromNamespaceAndPath("minecraft","textures/block/sculk_sensor_top.png");
    }
}

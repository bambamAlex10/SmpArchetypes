package com.mc3699.smparch.archetype.pinky;

import com.mc3699.smparch.SMPArch;

import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.Vec3;

public class HolyLight extends BaseAbility {

    @Override
    public boolean canExecute(ServerPlayer Player) {
        return true;    
    }

    @Override
    public Component getName() {
        return Component.literal("Holy Light");
    }

    @Override
    public float getUseCost() {
        return 1.5f;
    }


    @Override
    public int getCooldown() {
        return 20*20;
    }

    @Override
    public void execute(ServerPlayer player) {
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20*5, 3));
    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.fromNamespaceAndPath(SMPArch.MODID, "textures/ability_icon/holy_light.png");
    }
}

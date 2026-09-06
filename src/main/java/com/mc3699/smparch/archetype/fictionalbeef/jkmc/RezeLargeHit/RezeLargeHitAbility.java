package com.mc3699.smparch.archetype.fictionalbeef.jkmc.RezeLargeHit;

import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.archetype.fictionalbeef.jkmc.JkmcAttachments;
import com.mc3699.smparch.archetype.fictionalbeef.jkmc.ExplosionPassive.ExplosivePassiveHandlers;

public class RezeLargeHitAbility extends BaseAbility {
    public static final float oneMeter = ExplosivePassiveHandlers.oneMeter;


    @Override
    public boolean canExecute(ServerPlayer Player) {
        return ((Player.hasData(JkmcAttachments.JKMC_CHARGE) && (!Player.hasData(JkmcAttachments.JKMC_LARGEHIT) || !Player.getData(JkmcAttachments.JKMC_LARGEHIT))) && Player.getData(JkmcAttachments.JKMC_CHARGE) > (oneMeter*5f));
    }

    @Override
    public int getCooldown() {
        // return 10;
        return 60*20; // minute
    }

    @Override
    public Component getName() {
        return Component.literal("LargeHit");
    }

    @Override
    public float getUseCost() {
        return 0f;
    }

    public void execute(ServerPlayer Player) {
        Player.setData(JkmcAttachments.JKMC_LARGEHIT,true);
    }

    
    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.fromNamespaceAndPath(SMPArch.MODID, "textures/ability_icon/jkmc/murderblast.png");
    }
    
}

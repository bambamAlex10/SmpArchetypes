package com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidStep;

import java.util.Optional;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.archetype.fictionalbeef.scapune.DecieversGrace.DecieversGhost.DecieversGhostEntity;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.YkorioAttachments;
import com.mc3699.smparch.registry.SMPAttachments;

import net.mc3699.provenance.ability.foundation.AmbientAbility;
import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class VoidStepAbility extends BaseAbility {
    public static int GhostDuration = 20*5;

    @Override
    public boolean canExecute(ServerPlayer player) {
        return !player.hasData(YkorioAttachments.VOID_STEP);
    }

    @Override
    public Component getName() {
        return Component.literal("Void Step");
    }

    @Override
    public float getUseCost() {
        return 1.5f;
    }
    
    @Override
    public int getCooldown() {
        return 45 * 20;
    }

    public void execute(ServerPlayer player) {
        super.execute(player);
        player.setData(YkorioAttachments.VOID_STEP,true);
        player.syncData(YkorioAttachments.VOID_STEP);



    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.fromNamespaceAndPath(SMPArch.MODID, "textures/ability_icon/ykorio/voidstep.png");
    }
}
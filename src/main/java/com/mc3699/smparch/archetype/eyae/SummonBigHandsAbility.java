package com.mc3699.smparch.archetype.eyae;

import com.mc3699.smparch.SMPArch;
import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

public class SummonBigHandsAbility extends BaseAbility {
    @Override
    public float getUseCost() {
        return 1f;
    }

    @Override
    public int getCooldown() { return 120; }

    @Override
    public Component getName() {
        return Component.literal("Summon Big Hands");
    }

    @Override
    public void execute(ServerPlayer player) {
        super.execute(player);
    }

    @Override
    public boolean canExecute(ServerPlayer serverPlayer) {
        return true;
    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.fromNamespaceAndPath(SMPArch.MODID, "textures/ability_icon/terminal.png");
    }
}
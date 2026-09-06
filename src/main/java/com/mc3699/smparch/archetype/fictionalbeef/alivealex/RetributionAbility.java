package com.mc3699.smparch.archetype.fictionalbeef.alivealex;

import java.time.Instant;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.registry.SMPAttachments;
import com.mc3699.smparch.registry.SMPSounds;

import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

public class RetributionAbility extends BaseAbility {
    @Override
    public boolean canExecute(ServerPlayer Player) {
        return !(Player.hasData(AliveAlexAttachments.ALREADY_DEAD.get())) && !(Player.hasData(AliveAlexAttachments.LAST_POS.get()));
    }

    @Override
    public Component getName() {
        return Component.literal("Retribution");
    }

    @Override
    public float getUseCost() {
        return 3.5f;
    }

    @Override
    public int getCooldown() {
        return 5 * 20;
    }

    public void execute(ServerPlayer player) {
        super.execute(player);
        player.setData(AliveAlexAttachments.ALREADY_DEAD.get(), Instant.now());

    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.fromNamespaceAndPath(SMPArch.MODID, "textures/ability_icon/alivealex/retribution.png");
    }
}

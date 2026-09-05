package com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidEssence;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.YkorioAttachments;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.YkorioSounds;
import com.mc3699.smparch.registry.SMPAttachments;

import net.mc3699.provenance.ability.foundation.AmbientAbility;
import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;

public class VoidEssenceAbility extends BaseAbility {

    @Override
    public boolean canExecute(ServerPlayer player) {
        return !player.hasData(YkorioAttachments.HIT_DEFENSE);
    }

    @Override
    public Component getName() {
        return Component.literal("Void Essence");
    }

    @Override
    public float getUseCost() {
        return 1.5f;
    }
    
    @Override
    public int getCooldown() {
        return 45 * 20;
    }

    public static void staticExecute(ServerPlayer player) {
        player.setData(YkorioAttachments.HIT_DEFENSE,2);
        player.syncData(YkorioAttachments.HIT_DEFENSE);
        player.level().playSound(null, player.getOnPos(), YkorioSounds.SHIELDUP.value(),
                        SoundSource.PLAYERS, .4f, 1f);

    }

    public void execute(ServerPlayer player) {
        super.execute(player);
        
        player.setData(YkorioAttachments.HIT_DEFENSE,2);
        player.syncData(YkorioAttachments.HIT_DEFENSE);
        player.level().playSound(null, player.getOnPos(), YkorioSounds.SHIELDUP.value(),
                        SoundSource.PLAYERS, .4f, 1f);
    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.fromNamespaceAndPath(SMPArch.MODID, "textures/ability_icon/ykorio/voidessence.png");
    }
}

//todo: needs sfx & visuals for shield coming up and down as well as the shield itself
package com.mc3699.smparch.archetype.fictionalbeef.flamek1ng_.Pyrophagia;

import java.time.Instant;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.archetype.fictionalbeef.flamek1ng_.FlameK1ng_Attachments;
import com.mc3699.smparch.archetype.fictionalbeef.flamek1ng_.Brimstone.BrimstoneHandlers;
import com.mojang.logging.LogUtils;

import net.mc3699.provenance.ability.foundation.AmbientAbility;
import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.mc3699.provenance.ability.foundation.ToggleAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class PyrophagiaPassive extends ToggleAbility {

    // private boolean isOn = false;

    @Override
    public ResourceLocation getIcon() {
        // if (!isOn) {
        return ResourceLocation.fromNamespaceAndPath(SMPArch.MODID, "textures/ability_icon/flamek1ng_/pyrophagia.png");
        // } else {
        // return ResourceLocation.fromNamespaceAndPath(SMPArch.MODID, "textures/ability_icon/flamek1ng_/pyrophagiaoff.png");

        // }
    }
    
    // ResourceLocation myself = this;

    // @Override
    // public void tick(ServerPlayer player) {
    //     // player.clearFire();
    //     // player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE,1*20,10));
    // }
    @Override
    public void setEnabled(ServerPlayer player, ResourceLocation id, boolean enabled) {
        super.setEnabled(player, id, enabled);
        // isOn = enabled;
    }

    @Override
    public boolean canExecute(ServerPlayer player) {
        return true;
    }

    @Override
    public Component getName() {
        return Component.literal("Pyrophagia");
    }

    // @Override
    // public float getUseCost() {
    //         return 0.0f;
    // }

    @Override
    public int getCooldown() {
        return 8 * 60 * 20;
    }
    
    // public void correctExecute(ServerPlayer player) {

        // boolean heads = BrimstoneHandlers.flipCoin(player);

        // player.setData(FlameK1ng_Attachments.PYROPHAGIA,Instant.now());
    // }

    @Override
    public void tick(ServerPlayer player) {

        if (player.level().getGameTime()%20 != 0) {
            return;
        }

        LogUtils.getLogger().debug("yum yum!");

        int currentFood = player.getFoodData().getFoodLevel();
        float currentSat = player.getFoodData().getSaturationLevel();

        if (currentFood <= 1 && currentSat <= 0.5f) {
            player.setData(FlameK1ng_Attachments.PYROPHAGIA,Instant.now().plusSeconds(480));
        } else {
            if (currentSat >= 0) {
                player.getFoodData().setSaturation(currentSat-.25f);
            } else {
                player.getFoodData().setFoodLevel(currentFood-1);
            }
        }

        // if (player.hasData(FlameK1ng_Attachments.PYROPHAGIA) && (Instant.now().isAfter(player.getData(FlameK1ng_Attachments.PYROPHAGIA)) || (Instant.now() == player.getData(FlameK1ng_Attachments.PYROPHAGIA)))) {
        //     return;
        // } else {
        //     boolean shouldRun = player.getHealth()<(player.getMaxHealth()/3f);
        
        //     if (!shouldRun && Instant.now().isBefore(player.getData(FlameK1ng_Attachments.PYROPHAGIA))) {
        //         return;
        //     }

        //     correctExecute(player);
        // }
        
    }

    @Override
    public float getUseCost() {
        return 0.0f;
    }

}

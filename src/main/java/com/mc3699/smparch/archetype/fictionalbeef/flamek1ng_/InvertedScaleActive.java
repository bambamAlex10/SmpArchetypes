package com.mc3699.smparch.archetype.fictionalbeef.flamek1ng_;

import java.time.Instant;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.archetype.fictionalbeef.flamek1ng_.Brimstone.BrimstoneHandlers;
import com.mc3699.smparch.archetype.fictionalbeef.flamek1ng_.InvertedScale.InvertedScaleHandlers;
import com.mc3699.smparch.util.DelayedTaskHandler;

import net.mc3699.provenance.ability.foundation.AmbientAbility;
import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class InvertedScaleActive extends BaseAbility {

    // @Override
    // public void tick(ServerPlayer player) {
    //     // player.clearFire();
    //     // player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE,1*20,10));
    // }

    @Override
    public boolean canExecute(ServerPlayer player) {
        return true;
    }

    @Override
    public Component getName() {
        return Component.literal("Inverted Scale");
    }

    @Override
    public float getUseCost() {
        return 3.0f;
    }

    @Override
    public int getCooldown() {
        return 30 * 20;
    }
    
    public void execute(ServerPlayer player) {
        super.execute(player);

        DelayedTaskHandler.scheduleDelayed( 20, () -> {
            boolean heads = BrimstoneHandlers.flipCoin(player);

            long millis = heads ? 16000 : 6000;

            InvertedScaleHandlers.startCounter(player, Instant.now().plusMillis(millis));
        });
    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.fromNamespaceAndPath(SMPArch.MODID, "textures/ability_icon/flamek1ng_/invertedscale.png");
    }
}

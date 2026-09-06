package com.mc3699.smparch.archetype.fictionalbeef.flamek1ng_.Brimstone;

import net.mc3699.provenance.ability.foundation.AmbientAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
public class BrimstonePassive extends AmbientAbility {

    @Override
    public void tick(ServerPlayer player) {
        // player.clearFire();
        // player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE,1*20,10));
    }

    @Override
    public boolean canExecute(ServerPlayer player) {
        return true;
    }

    @Override
    public Component getName() {
        return Component.literal("Brimstone");
    }
    
}

package com.mc3699.smparch.archetype.tbs.miku;

import net.mc3699.provenance.ability.foundation.AmbientAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class OneWithTheDark extends AmbientAbility {
    private int tickCount = 0;
    @Override
    public void tick(ServerPlayer player) {
        player.removeEffect(MobEffects.DARKNESS);
        player.removeEffect(MobEffects.BLINDNESS);
        tickCount++;
        if(tickCount > 20)
        {
            MobEffectInstance haste = new MobEffectInstance(MobEffects.DIG_SPEED, -1, 0, true, false, true);
            player.addEffect(haste);
            tickCount = 0;
        }
    }

    @Override
    public Component getName() {
        return Component.literal("From the caves, the skulk emerges");
    }

    @Override
    public boolean canExecute(ServerPlayer player) { return true; }
}

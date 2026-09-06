package com.mc3699.smparch.archetype.zorgoliath;

import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class HarkenerWrathfulAdvance extends BaseAbility {
    @Override
    public float getUseCost() {
        return 1.85f;
    }

    @Override
    public Component getName() {
        return Component.literal("Wrathful Advance");
    }

    @Override
    public boolean canExecute(ServerPlayer serverPlayer) {
        return true;
    }

    @Override
    public void execute(ServerPlayer player) {
        super.execute(player);
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 240, 1));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 240, 1));
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 240, 0));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 240, 0));
    }

    @Override
    public int getCooldown() {
        return 1000;
    }
}

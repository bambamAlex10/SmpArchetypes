package com.mc3699.smparch.archetype.tbs.blox;

import net.mc3699.provenance.ability.foundation.ToggleAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;

public class BloxSpeedAbility extends ToggleAbility {

    private int leggingDamageCount = 10;

    @Override
    public float getUseCost() {
        return 0;
    }

    @Override
    public Component getName() {
        return Component.literal("Speed");
    }

    @Override
    public void tick(ServerPlayer serverPlayer) {
        leggingDamageCount--;
        if(leggingDamageCount < 1) {
            serverPlayer.getItemBySlot(EquipmentSlot.LEGS).hurtAndBreak(1, serverPlayer, EquipmentSlot.LEGS);
            serverPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20, 3));
            leggingDamageCount = 10;
        }
    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.fromNamespaceAndPath("minecraft","textures/mob_effect/speed.png");
    }

    @Override
    public boolean canExecute(ServerPlayer serverPlayer) {
        return BloxArchetype.checkArmor(serverPlayer);
    }
}

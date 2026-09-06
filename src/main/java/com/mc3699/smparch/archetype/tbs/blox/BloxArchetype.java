package com.mc3699.smparch.archetype.tbs.blox;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.registry.SMPAbilities;
import net.mc3699.provenance.ability.foundation.AmbientAbility;
import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.mc3699.provenance.archetype.foundation.BaseArchetype;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class BloxArchetype extends BaseArchetype {
    @Override
    public Component getName() {
        return Component.literal("Blox");
    }

    @Override
    public List<Component> getDescription() {
        return List.of();
    }

    @Override
    public Set<ResourceLocation> getGrantedAbilities() {
        return Set.of(
                SMPArch.path("blox_dash"),
                SMPArch.path("blox_speed"),
                SMPArch.path("blox_shield")
        );
    }

    @Override
    public List<AmbientAbility> getAmbientAbilities() {
        return List.of();
    }

    public static boolean checkArmor(ServerPlayer serverPlayer)
    {
        return !serverPlayer.getItemBySlot(EquipmentSlot.HEAD).isEmpty() &&
                !serverPlayer.getItemBySlot(EquipmentSlot.CHEST).isEmpty() &&
                !serverPlayer.getItemBySlot(EquipmentSlot.LEGS).isEmpty() &&
                !serverPlayer.getItemBySlot(EquipmentSlot.FEET).isEmpty();
    }
}

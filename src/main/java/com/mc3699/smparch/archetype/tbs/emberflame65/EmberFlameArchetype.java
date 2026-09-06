package com.mc3699.smparch.archetype.emberflame65;

import com.mc3699.smparch.SMPArch;
import net.mc3699.provenance.ability.foundation.AmbientAbility;
import net.mc3699.provenance.archetype.foundation.BaseArchetype;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Set;

public class EmberFlameArchetype extends BaseArchetype {
    @Override
    public Component getName() {
        return Component.literal("65Flame");
    }

    @Override
    public List<Component> getDescription() {
        return List.of();
    }

    @Override
    public Set<ResourceLocation> getGrantedAbilities() {
        return Set.of(
                SMPArch.path("flight"),
                SMPArch.path("flight_boost"),
                SMPArch.path("sustained_flight"),
                SMPArch.path("bird_song"),
                SMPArch.path("jump_boost")
        );
    }

    @Override
    public List<AmbientAbility> getAmbientAbilities() {
        return List.of();
    }
}

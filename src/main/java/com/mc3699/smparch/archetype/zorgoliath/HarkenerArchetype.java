package com.mc3699.smparch.archetype.zorgoliath;

import com.mc3699.smparch.SMPArch;
import net.mc3699.provenance.ability.foundation.AmbientAbility;
import net.mc3699.provenance.archetype.foundation.BaseArchetype;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Set;

public class HarkenerArchetype extends BaseArchetype {
    @Override
    public Component getName() {
        return Component.literal("Harkener");
    }

    @Override
    public List<Component> getDescription() {
        return List.of();
    }

    @Override
    public Set<ResourceLocation> getGrantedAbilities() {
        return Set.of(
                SMPArch.path("true_sight"),
                SMPArch.path("wall_climb"),
                SMPArch.path("silent_dash"),
                SMPArch.path("wrathful_advance")
        );
    }

    @Override
    public List<AmbientAbility> getAmbientAbilities() {
        return List.of();
    }
}

package com.mc3699.smparch.archetype.eyae;

import com.mc3699.smparch.SMPArch;
import net.mc3699.provenance.ability.foundation.AmbientAbility;
import net.mc3699.provenance.archetype.foundation.BaseArchetype;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Set;

public class EyaeArchetype extends BaseArchetype {
    @Override
    public Component getName() {
        return Component.literal("Eyae");
    }

    @Override
    public List<Component> getDescription() {
        return List.of();
    }

    @Override
    public Set<ResourceLocation> getGrantedAbilities() {
        return Set.of(
                SMPArch.path("summon_big_hands"),
                SMPArch.path("dash")
        );
    }

    @Override
    public List<AmbientAbility> getAmbientAbilities() {
        return List.of();
    }
}

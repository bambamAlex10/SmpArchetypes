package com.mc3699.smparch.archetype.fictionalbeef.bigmanrake;

import java.util.List;
import java.util.Set;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.registry.SMPAbilities;

import net.mc3699.provenance.ability.foundation.AmbientAbility;
import net.mc3699.provenance.archetype.foundation.BaseArchetype;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class BigManRakeArchetype extends BaseArchetype {
    @Override
    public Component getName() {
        return Component.literal("BigManRake");
    }

    @Override
    public List<Component> getDescription() {
        return List.of();
    }

    @Override
    public Set<ResourceLocation> getGrantedAbilities() {
        return Set.of(
                SMPArch.path("accelerando"),
                SMPArch.path("concerto"),
                SMPArch.path("vibrato")
        );
    }

    @Override
    public List<AmbientAbility> getAmbientAbilities() {
        return List.of(
            SMPAbilities.ACCESO_MORENDO.get()
        );
    }

}

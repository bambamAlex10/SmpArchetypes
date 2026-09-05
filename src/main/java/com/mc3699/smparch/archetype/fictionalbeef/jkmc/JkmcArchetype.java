package com.mc3699.smparch.archetype.fictionalbeef.jkmc;

import java.util.List;
import java.util.Set;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.registry.SMPAbilities;

import net.mc3699.provenance.ability.foundation.AmbientAbility;
import net.mc3699.provenance.archetype.foundation.BaseArchetype;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class JkmcArchetype extends BaseArchetype {
    @Override
    public Component getName() {
        return Component.literal("Jkmc");
    }

    @Override
    public List<Component> getDescription() {
        return List.of();
    }

    @Override
    public Set<ResourceLocation> getGrantedAbilities() {
        return Set.of(
                SMPArch.path("reze_large_hit"),
                SMPArch.path("reze_propulse")
        );
    }

    @Override
    public List<AmbientAbility> getAmbientAbilities() {
        return List.of(
            SMPAbilities.REZEPASSIVE.get(),
            SMPAbilities.REZEDEATHPASSIVE.get()
        );
    }

}

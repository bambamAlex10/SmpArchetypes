package com.mc3699.smparch.archetype.tbs.tekkitdooood;

import net.mc3699.provenance.ability.foundation.AmbientAbility;
import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.mc3699.provenance.archetype.foundation.BaseArchetype;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class TekkitdoooodArchetype extends BaseArchetype {
    @Override
    public Component getName() {
        return Component.literal("El doooodit");
    }

    @Override
    public List<Component> getDescription() {
        return List.of();
    }

    // u use empty set now lol
    @Override
    public Set<ResourceLocation> getGrantedAbilities() {
        return Set.of();
    }

    @Override
    public List<AmbientAbility> getAmbientAbilities() {
        return List.of();
    }
}

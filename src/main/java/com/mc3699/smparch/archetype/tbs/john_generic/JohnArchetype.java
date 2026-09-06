package com.mc3699.smparch.archetype.john_generic;

import com.mc3699.smparch.SMPArch;
import net.mc3699.provenance.ability.foundation.AmbientAbility;
import net.mc3699.provenance.archetype.foundation.BaseArchetype;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Set;

//A Goober for quick testing of the generic stuff
public class JohnArchetype extends BaseArchetype {
    @Override
    public Component getName() {
        return Component.literal("John Generic");
    }

    @Override
    public List<Component> getDescription() {
        return List.of();
    }

    @Override
    public Set<ResourceLocation> getGrantedAbilities() {
        return Set.of(
                SMPArch.path("arrow"),
                SMPArch.path("dash"),
                SMPArch.path("fireball"),
                SMPArch.path("wall_climb")
        );
    }

    @Override
    public List<AmbientAbility> getAmbientAbilities() {
        return List.of();
    }
}

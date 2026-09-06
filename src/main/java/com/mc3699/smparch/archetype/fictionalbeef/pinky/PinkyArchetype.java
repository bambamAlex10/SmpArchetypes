package com.mc3699.smparch.archetype.fictionalbeef.pinky;

//technically pinkys archetype was built for TBS but I dont think it got implemented so i've just put it in the fictionalbeef archetypes since it was never put on TBS.

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.registry.SMPAbilities;
import net.mc3699.provenance.ability.foundation.AmbientAbility;
import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.mc3699.provenance.archetype.foundation.BaseArchetype;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class PinkyArchetype extends BaseArchetype {
    @Override
    public Component getName() {
        return Component.literal("Pinky");
    }

    @Override
    public List<Component> getDescription() {
        return List.of(Component.literal("Pinkies Abilities"));
    }

    @Override
    public Set<ResourceLocation> getGrantedAbilities() {
        return Set.of(
                SMPArch.path("chaotic_surge"),
                SMPArch.path("holy_light"),
                SMPArch.path("void_blitz")
        );
    }

    @Override
    public List<AmbientAbility> getAmbientAbilities() {
        return List.of(
            SMPAbilities.CAT_LIKE.get()
        );
    }

}

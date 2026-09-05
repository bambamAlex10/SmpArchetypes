package com.mc3699.smparch.archetype.tbs.nightheart;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.registry.SMPAbilities;
import net.mc3699.provenance.ability.foundation.AmbientAbility;
import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.mc3699.provenance.archetype.foundation.BaseArchetype;
import net.mc3699.provenance.registry.ProvAbilities;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class NightheartArchetype extends BaseArchetype {
    @Override
    public Component getName() {
        return Component.literal("Nightheart");
    }

    @Override
    public List<Component> getDescription() {
        return List.of(
                Component.literal("Made for DrNightheart")
        );
    }

    @Override
    public Set<ResourceLocation> getGrantedAbilities() {
        return Set.of(
                SMPArch.path("random_teleport")
        );
    }

    @Override
    public List<AmbientAbility> getAmbientAbilities() {
        return List.of();
    }
}


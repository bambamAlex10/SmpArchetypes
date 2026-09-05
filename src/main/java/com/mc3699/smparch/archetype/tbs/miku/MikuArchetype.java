package com.mc3699.smparch.archetype.tbs.miku;

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

public class MikuArchetype extends BaseArchetype {
    @Override
    public Component getName() {
        return Component.literal("Miku");
    }

    @Override
    public List<Component> getDescription() {
        return List.of();
    }

    @Override
    public Set<ResourceLocation> getGrantedAbilities() {
        return Set.of(
                SMPArch.path("skulk_blast"),
                SMPArch.path("warden_skin"),
                SMPArch.path("warden_strength"),
                SMPArch.path("sonic_boom"),
                SMPArch.path("strong_legs_heavy_arms"),
                SMPArch.path("deep_darkness"),
                SMPArch.path("aid_from_below")
        );
    }

    @Override
    public List<AmbientAbility> getAmbientAbilities() {
        return List.of(
            SMPAbilities.ONE_WITH_THE_DARK.get()
        );
    }
}
package com.mc3699.smparch.archetype.fictionalbeef.xander;

import java.util.List;
import java.util.Set;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.registry.SMPAbilities;

import net.mc3699.provenance.ability.foundation.AmbientAbility;
import net.mc3699.provenance.archetype.foundation.BaseArchetype;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class XanderArchetype extends BaseArchetype {
@Override
    public Component getName() {
        return Component.literal("Xander");
    }

    @Override
    public List<Component> getDescription() {
        return List.of();
    }

    @Override
    public Set<ResourceLocation> getGrantedAbilities() {
        return Set.of(
                SMPArch.path("megiddo"),
                SMPArch.path("teleport"),
                SMPArch.path("beezlebub")
        );
    }

    @Override
    public List<AmbientAbility> getAmbientAbilities() {
        return List.of(
            SMPAbilities.DECIVRSAWARENESS.get()
        );
    }
}

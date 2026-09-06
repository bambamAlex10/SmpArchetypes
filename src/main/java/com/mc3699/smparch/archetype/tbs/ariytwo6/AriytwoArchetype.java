package com.mc3699.smparch.archetype.ariytwo6;

import com.mc3699.smparch.SMPArch;
import net.mc3699.provenance.archetype.foundation.BaseArchetype;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Set;

public class AriytwoArchetype extends BaseArchetype {
    @Override
    public Component getName() {
        return Component.literal("AriyTwo6");
    }

    @Override
    public Set<ResourceLocation> getGrantedAbilities() {
        return Set.of(
                        SMPArch.path("home_coming"),
                        SMPArch.path("water_jump"),
                        SMPArch.path("ground_punch"),
                        SMPArch.path("strength_random")
                     );
    }
}

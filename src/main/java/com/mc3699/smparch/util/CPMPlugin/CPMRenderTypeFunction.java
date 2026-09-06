package com.mc3699.smparch.util.CPMPlugin;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import java.util.function.Function;

import javax.swing.text.html.parser.Entity;

public class CPMRenderTypeFunction implements Function<ResourceLocation, RenderType> {
    @Override
    public RenderType apply(ResourceLocation texture) {
        return RenderType.entityTranslucent(texture);
    }
    Entity a;
}
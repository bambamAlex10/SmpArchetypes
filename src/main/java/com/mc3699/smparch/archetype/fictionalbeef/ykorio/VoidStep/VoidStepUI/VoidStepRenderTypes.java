package com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidStep.VoidStepUI;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;

public class VoidStepRenderTypes extends RenderType {
    private VoidStepRenderTypes(String n, VertexFormat f, VertexFormat.Mode m,
                                int s, boolean c, boolean o, Runnable a, Runnable b) {
        super(n, f, m, s, c, o, a, b); // never instantiated
    }

    public static final RenderType HIGHLIGHT_FILL = create(
            "voidstep_highlight",
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.QUADS,
            1536,
            false,  // affectsCrumbling
            true,   // sortOnUpload — proper back-to-front alpha blending
            CompositeState.builder()
                    .setShaderState(POSITION_COLOR_SHADER)
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setCullState(NO_CULL)                    // visible from inside too
                    .setDepthTestState(LEQUAL_DEPTH_TEST)
                    .setWriteMaskState(COLOR_WRITE)           // depth test, no depth write
                    .setLayeringState(VIEW_OFFSET_Z_LAYERING) // kills z-fighting properly
                    .createCompositeState(false));
}
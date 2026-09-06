package com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidEssence;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

    
public class GradientVertexConsumer implements VertexConsumer {
    private final VertexConsumer delegate;
    private final float minY;    // feet
    private final float maxY;    // head
    private final int baseRgb;
    private final float timeOffset;  // ← new: oscillating value (0..1)

    public GradientVertexConsumer(VertexConsumer delegate, float minY, float maxY, int baseRgb, float timeOffset) {
        this.delegate = delegate;
        this.minY = minY;
        this.maxY = maxY;
        this.baseRgb = baseRgb;
        this.timeOffset = timeOffset;
    }
private int getAlpha(float y) {
    float t = (y - minY) / (maxY - minY);   // 0 feet, 1 head
    // Sine wave: alpha = 0.5 + 0.5 * sin(2π * (t - timeOffset))
    float angle = (float)(2 * Math.PI * (t - timeOffset));
    float alphaFactor = 0.5f + 0.5f * (float)Math.sin(angle);
    int alpha = (int) (alphaFactor * 0xCC);
    return Math.min(255, Math.max(0, alpha));
}

    // Combine RGB with computed alpha
    private int getColor(float y) {
        int alpha = getAlpha(y);
        return (alpha << 24) | (baseRgb & 0x00FFFFFF);
    }

    @Override
    public VertexConsumer addVertex(Matrix4f pose, float x, float y, float z) {
        // y is the vertex coordinate in model space (feet ~0, head ~1.8)
        int rgba = getColor(y);
        float r = ((rgba >> 16) & 0xFF) / 255.0f;
        float g = ((rgba >> 8) & 0xFF) / 255.0f;
        float b = (rgba & 0xFF) / 255.0f;
        float a = ((rgba >> 24) & 0xFF) / 255.0f;
        delegate.addVertex(pose, x, y, z).setColor(r, g, b, a);
        return this;
    }

    @Override public VertexConsumer setColor(int r, int g, int b, int a) { delegate.setColor(r, g, b, a); return this; }
    @Override public VertexConsumer setUv(float u, float v) { delegate.setUv(u, v); return this; }
    @Override public VertexConsumer setUv2(int u, int v) { delegate.setUv2(u, v); return this; }

    @Override
    public VertexConsumer addVertex(float x, float y, float z) {
        int rgba = getColor(y);
        float r = ((rgba >> 16) & 0xFF) / 255.0f;
        float g = ((rgba >> 8) & 0xFF) / 255.0f;
        float b = (rgba & 0xFF) / 255.0f;
        float a = ((rgba >> 24) & 0xFF) / 255.0f;
        delegate.addVertex(x, y, z).setColor(r, g, b, a);
        return this;
    }

    @Override
    public VertexConsumer setNormal(float nx, float ny, float nz) {
        delegate.setNormal(nx, ny, nz);
        return this;
    }

    @Override
    public VertexConsumer setUv1(int u, int v) {
        delegate.setUv1(u,v);
return this;
    }
}

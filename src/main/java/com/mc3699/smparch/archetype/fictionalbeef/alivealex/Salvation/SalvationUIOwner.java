package com.mc3699.smparch.archetype.fictionalbeef.alivealex.Salvation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.archetype.fictionalbeef.alivealex.AliveAlexAttachments;
import com.mc3699.smparch.archetype.fictionalbeef.alivealex.BloodofCovenant.BloodofCovenantHandlers;
import com.mc3699.smparch.packets.displayTargetDamage;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import com.mojang.math.Axis;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.api.distmarker.Dist;

@EventBusSubscriber(modid = "smparch", value = Dist.CLIENT)
public class SalvationUIOwner {
    private static final List<RetributionCount> activeUI = new LinkedList<>();
    private static final Matrix4f projectionMatrix = new Matrix4f();
    private static int Color = 0x00FFFF;

    public static List<RetributionCount> getList() {
        return activeUI;
    }

    public static void newUI(int entityID, float value) {
        for (RetributionCount existing : activeUI) {
            if (existing.entityID == entityID) {
                existing.value += value;
                Entity entity = Minecraft.getInstance().level.getEntity(entityID);
                if (entity != null) {
                    existing.worldPos = entity.getEyePosition().add(0, 0.5, 0);
                }
                return;
            }
        }

        Entity target = Minecraft.getInstance().level.getEntity(entityID);
        Vec3 pos = target.getEyePosition().add(0, 0.5, 0);
        activeUI.add(new RetributionCount(value, entityID, pos));

    }

    public static void tick() {
        if (activeUI.isEmpty()) {
            return;
        }

        if (!Minecraft.getInstance().player.hasData(AliveAlexAttachments.SALVATION)) {
            activeUI.clear();
            return;
        }

        if (BloodofCovenantHandlers.isholdingWeapon(Minecraft.getInstance().player)) {
            Color = 0xFF0000;
        } else {
            Color = 0x00FFFF;
        }
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            projectionMatrix.set(RenderSystem.getProjectionMatrix());
        }
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null)
            return;

        Camera camera = mc.gameRenderer.getMainCamera();
        Window window = mc.getWindow();
        Font font = mc.font;
        PoseStack poseStack = event.getGuiGraphics().pose();
        MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();

        double fov = mc.options.fov().get();
        float aspect = (float) window.getWidth() / window.getHeight();
        Matrix4f projMatrix = new Matrix4f().perspective(
                (float) Math.toRadians(fov), aspect, 0.05f, 1024.0f);

        Vec3 camPos = camera.getPosition();
        Matrix4f viewMatrix = new Matrix4f()
                .rotate(camera.rotation().conjugate()) // inverse of camera world rotation
                .translate((float) -camPos.x, (float) -camPos.y, (float) -camPos.z);

        Matrix4f mvp = new Matrix4f(projMatrix);
        mvp.mul(viewMatrix);

        List<RetributionCount> snapshot = new ArrayList<>(activeUI);
        for (RetributionCount element : snapshot) {
            float progress = 0f;
            if (progress >= 1.0f)
                continue;

            Entity entity = mc.level.getEntity(element.entityID);
            if (entity == null)
                continue;

            element.worldPos = entity.getEyePosition().add(0, .2, 0);

            Vec3 screenPos = projectToScreen(element.worldPos, mvp, window);
            if (screenPos == null)
                continue;

            int alpha = (int) ((1.0f - progress) * 255);
            if (alpha <= 0)
                continue;
            int color = (alpha << 24) | Color;

            String text = String.valueOf(element.value);
            int textWidth = font.width(text);
            float x = (float) screenPos.x - textWidth / 2f;
            float y = (float) screenPos.y - 10;

            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            font.drawInBatch(text, x, y, color, false,
                    poseStack.last().pose(), buffers,
                    Font.DisplayMode.NORMAL, 0, 0xF000F0);
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
        }
        buffers.endBatch();
    }

    private static Vec3 projectToScreen(Vec3 worldPos, Matrix4f mvp, Window window) {
        org.joml.Vector4f clip = new org.joml.Vector4f(
                (float) worldPos.x, (float) worldPos.y, (float) worldPos.z, 1.0f);
        mvp.transform(clip);

        if (clip.w <= 0)
            return null;

        float ndcX = clip.x / clip.w;
        float ndcY = clip.y / clip.w;

        double screenX = (ndcX + 1.0) * window.getGuiScaledWidth() / 2.0;
        double screenY = (1.0 - ndcY) * window.getGuiScaledHeight() / 2.0;

        return new Vec3(screenX, screenY, clip.w);
    }

    public static void retributionHandler(final displayTargetDamage packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            SalvationUIOwner.newUI(packet.entity(), packet.damage());
        });
    }

    public static class RetributionCount {
        public Vec3 worldPos;
        public int entityID;
        public float value;

        public RetributionCount(float value, int entity, Vec3 position) {
            this.value = value;
            this.worldPos = position;
            this.entityID = entity;
        }
    }

}

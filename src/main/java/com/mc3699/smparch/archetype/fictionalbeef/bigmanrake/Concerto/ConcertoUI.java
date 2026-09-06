package com.mc3699.smparch.archetype.fictionalbeef.bigmanrake.Concerto;

import java.util.function.Predicate;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.slf4j.Logger;

import com.jcraft.jogg.Buffer;
import com.mc3699.smparch.archetype.fictionalbeef.bigmanrake.BigManRakeAttachments;
import com.mc3699.smparch.registry.SMPSounds;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.logging.LogUtils;
import com.mojang.math.Axis;

import net.mc3699.provenance.ProvenanceDataHandler;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

public class ConcertoUI extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    private static final Logger LOGGER = LogUtils.getLogger();

    public ConcertoUI(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent) {
        super(parent);
    }

    private static record MovementData(ResourceLocation texture, float Size) {
    }
    private static record StatusPNG(ResourceLocation texture, Predicate<Float> valid, float Size) {
    }

    private static float timer = 0;

    public static final MovementData[] MovementPNGS = {
            new MovementData(ResourceLocation.fromNamespaceAndPath("smparch", "textures/misc/movement1.png"), 1),
            new MovementData(ResourceLocation.fromNamespaceAndPath("smparch", "textures/misc/movement2.png"), 2),
            new MovementData(ResourceLocation.fromNamespaceAndPath("smparch", "textures/misc/movement3.png"), 2.5f),
            new MovementData(ResourceLocation.fromNamespaceAndPath("smparch", "textures/misc/movement4.png"), 4),
    };

    public static final StatusPNG[] StatusPNGS = {
        new StatusPNG(ResourceLocation.fromNamespaceAndPath("smparch", "textures/gui/movement1text.png"), (Float) -> (Float == 0.0f+(1f/12f)),3),
        new StatusPNG(ResourceLocation.fromNamespaceAndPath("smparch", "textures/gui/movement2text.png"), (Float) -> (Float == 1.0f),3),
        new StatusPNG(ResourceLocation.fromNamespaceAndPath("smparch", "textures/gui/movement3text.png"), (Float) -> (Float == 2.0f),3),
        new StatusPNG(ResourceLocation.fromNamespaceAndPath("smparch", "textures/gui/movement4text.png"), (Float) -> (Float == 3.0f),3),
        new StatusPNG(ResourceLocation.fromNamespaceAndPath("smparch", "textures/gui/finaletext.png"), (Float) -> (Float == 4.3f),3),
    };

    private static final ResourceLocation CURTAINFULLCLOSED = ResourceLocation.fromNamespaceAndPath("smparch",
            "textures/gui/curtainfullclosed.png");
    private static final ResourceLocation CURTRAINOPEN = ResourceLocation.fromNamespaceAndPath("smparch",
            "textures/gui/curtainopen.png");

    public static LayeredDraw.Layer Movement1UI = (GuiGraphics graphics, DeltaTracker dt) -> {
        Player player = Minecraft.getInstance().player;

        boolean active = false;
        boolean showOpen = false;
        
        AABB area = player.getBoundingBox().inflate(18);

        for (net.minecraft.world.entity.Entity temp : player.level().getEntities(null, area)) {
            if (!(temp instanceof Player target)) {
                continue;
            }

            if (!target.hasData(BigManRakeAttachments.CONCERTO_STAGE)) {
                continue;
            }

            if (target.getData(BigManRakeAttachments.MOVEMENT) >= 5.0f) {
                timer = 2.0f;
                active = true;
                showOpen = false;
                break;
            } else if (target.getData(BigManRakeAttachments.MOVEMENT) >= 4f) {
                active = true;
                showOpen = true;
                break;
            }
        }

        if (timer > 0) {
            System.out.println("greater then 0");
            active = true;
            showOpen = false;
            timer -= dt.getRealtimeDeltaTicks()/20;
        }

        int width = graphics.guiWidth();
        int height = graphics.guiHeight();
        if (active) {
            graphics.blit(showOpen ? CURTRAINOPEN : CURTAINFULLCLOSED, 0,0, width, height, 0.0f, 0.0f, 64, 64, 64, 64);
        }
    };

    @Override
    public void render(PoseStack poseStack, MultiBufferSource Buffer, int PackedLight, AbstractClientPlayer Player,
            float LimbSwing, float LimbSwingAmont, float PartialTick, float AgeInTicks, float NetHeadYaw,
            float HeadPitch) {

        if (!Player.hasData(BigManRakeAttachments.MOVEMENT) || ((Player.getData(BigManRakeAttachments.MOVEMENT) % 10f) == 0)) {
            return;
        }

        float Movement = Player.getData(BigManRakeAttachments.MOVEMENT);
        Quaternionf CameraRotation = Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation();

        float spinSpeed = 2.0f;
        int increment = 0;

        for (MovementData PNG : MovementPNGS) {
            increment += 1;
            if ((float) PNG.Size - .95 > (Movement)) {
                continue;
            }

            float circleSpeed = spinSpeed;
            float tick = AgeInTicks + PartialTick;

            for (int i = 0; i < increment; i++) {
                circleSpeed *= -1;
            }

            float angle = tick * circleSpeed;

            try {
                poseStack.pushPose();
                poseStack.translate(0.0D, 0D, 0.0D);

                // float bodyRotation = Player.getViewYRot(0);
                // poseStack.mulPose(Axis.YP.rotationDegrees(-bodyRotation));

                Matrix4f matrix = poseStack.last().pose();

                matrix.m00(1.0f);
                matrix.m01(0.0f);
                matrix.m02(0.0f);
                matrix.m10(0.0f);
                matrix.m11(1.0f);
                matrix.m12(0.0f);
                matrix.m20(0.0f);
                matrix.m21(0.0f);
                matrix.m22(1.0f);

                poseStack.mulPose(CameraRotation);
                poseStack.mulPose(Axis.ZP.rotationDegrees(angle));

                float Solution = (1 + (Movement - increment));

                float CurrentIncrement = (int) Math.max(0, Math.min(254, 254 * Solution));
                VertexConsumer constructor;

                constructor = Buffer.getBuffer(RenderType.entityTranslucent(PNG.texture()));
                renderQuad(poseStack, constructor, PNG.Size * Math.min(1, Solution), 0xF000F0,
                        CurrentIncrement);
            } finally {
                poseStack.popPose();
            }
        }
        for (StatusPNG PNG : StatusPNGS) {
            if (!PNG.valid.test((Float) Movement)) {
                continue;
            }

            float NewerMovement = ((Player.level().getGameTime()-Player.getData(BigManRakeAttachments.START_TICK)/(6f*20f)));

            increment += 1;

            try {
                poseStack.pushPose();
                poseStack.translate(0.0D, 0D, 0.0D);

                // float bodyRotation = Player.getViewYRot(0);
                // poseStack.mulPose(Axis.YP.rotationDegrees(-bodyRotation));

                Matrix4f matrix = poseStack.last().pose();

                matrix.m00(1.0f);
                matrix.m01(0.0f);
                matrix.m02(0.0f);
                matrix.m10(0.0f);
                matrix.m11(1.0f);
                matrix.m12(0.0f);
                matrix.m20(0.0f);
                matrix.m21(0.0f);
                matrix.m22(1.0f);

                poseStack.mulPose(CameraRotation);

                float Solution = Math.clamp(NewerMovement,0.0f,1.0f);

                float CurrentIncrement = (int) Math.max(0, Math.min(254, 254 * Solution));
                VertexConsumer constructor;

                constructor = Buffer.getBuffer(RenderType.entityTranslucent(PNG.texture()));
                renderQuad(poseStack, constructor, PNG.Size * Math.min(1, Solution), 0xF000F0,
                        CurrentIncrement);
            } finally {
                poseStack.popPose();
            }
        }
    }

    private void renderQuad(PoseStack poseStack, VertexConsumer constructor, float s, int light, float transparency) {
        PoseStack.Pose last = poseStack.last();
        Matrix4f matrix = last.pose();

        addVertex(constructor, matrix, last, -s, -s, 0, 0, 1, light, transparency);
        addVertex(constructor, matrix, last, s, -s, 0, 1, 1, light, transparency);
        addVertex(constructor, matrix, last, s, s, 0, 1, 0, light, transparency);
        addVertex(constructor, matrix, last, -s, s, 0, 0, 0, light, transparency);
    }

    private void addVertex(VertexConsumer buffer, Matrix4f matrix, PoseStack.Pose last, float x, float y, float z,
            float u, float v, int light, float multiplier) {

        float Max = ((255) - (multiplier));

        buffer.addVertex(matrix, x, y, z)
                .setColor(Max, Max, Max, Max)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightTexture.FULL_BRIGHT)
                .setNormal(last, 0.0f, 1.0f, 0.0f);

    }

}

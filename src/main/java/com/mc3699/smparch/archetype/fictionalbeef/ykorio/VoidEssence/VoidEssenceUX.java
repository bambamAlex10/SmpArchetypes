package com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidEssence;

import com.mc3699.smparch.archetype.fictionalbeef.ykorio.YkorioAttachments;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import com.mojang.math.Axis;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.mc3699.smparch.SMPArch;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.api.distmarker.Dist;
import net.minecraft.client.renderer.GameRenderer;

@EventBusSubscriber(modid = SMPArch.MODID, value = Dist.CLIENT)
public class VoidEssenceUX {
    private static final ResourceLocation WHITE_TEXTURE = ResourceLocation
            .withDefaultNamespace("textures/misc/white.png");

    private static final RenderType GHOST_RENDER_TYPE = RenderType.create(
            "ghost_overlay",
            DefaultVertexFormat.NEW_ENTITY,
            VertexFormat.Mode.QUADS,
            256,
            false,
            false,
            RenderType.CompositeState.builder()
                    .setTextureState(new RenderStateShard.TextureStateShard(WHITE_TEXTURE, false, false))
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setDepthTestState(new RenderStateShard.DepthTestStateShard("lequal", 515))
                    .setCullState(RenderStateShard.NO_CULL)
                    .setLightmapState(RenderStateShard.LIGHTMAP)
                    .setOverlayState(RenderStateShard.OVERLAY)
                    .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
                    .createCompositeState(false));

@SubscribeEvent
public static void onRenderLivingPost(RenderLivingEvent.Post<?, ?> event) {
    if (!(event.getEntity() instanceof Player player)) return;
    if (!player.hasData(YkorioAttachments.HIT_DEFENSE)) return;
    float defense = player.getData(YkorioAttachments.HIT_DEFENSE);
    if (defense <= 0) return;
    if (!(event.getRenderer().getModel() instanceof PlayerModel<?> model)) return;
 
    float partialTick = event.getPartialTick();
    PoseStack poseStack = event.getPoseStack();
 
    // NOTE: the PoseStack is ALREADY at the player's interpolated render
    // position (EntityRenderDispatcher translated it, including the sneak
    // offset from getRenderOffset). Do NOT translate to the player again.
 
    float ghostScale = 1F; // shell size; 1.15-1.4 gives a tighter "aura" fit
 
    poseStack.pushPose();
 
    // Optional: center the enlarged shell on the body instead of keeping the
    // feet planted at ground level. Delete this line to keep feet planted.
    poseStack.translate(0.0F, -(ghostScale - 1.0F) * player.getBbHeight() * 0.5F, 0.0F);
 
    // --- verbatim vanilla rotation setup (PlayerRenderer.setupRotations) ---
    float bodyYaw = Mth.rotLerp(partialTick, player.yBodyRotO, player.yBodyRot);
    setupRotations(player, poseStack, bodyYaw, partialTick);
 
    // --- verbatim vanilla model-space setup (LivingEntityRenderer.render) ---
    poseStack.scale(-1.0F, -1.0F, 1.0F);        // models are built x-mirrored, y-down
    poseStack.scale(ghostScale, ghostScale, ghostScale);
    poseStack.translate(0.0F, -1.501F, 0.0F);   // vanilla model y-offset
 
    // Do NOT call setupAnim here. This is the exact model instance vanilla
    // just posed and rendered this frame, so its pose already matches the
    // on-screen player perfectly. Re-posing it is what desyncs limbs/head.
 
    int alpha = Mth.clamp((int) (defense * 50.0F), 0, 255);
    int color = (alpha << 24) | 0x8800FF;
 
    float cycleTicks = 40.0F; // 2s cycle, tick-based so it pauses with the game
    float offset = ((player.tickCount + partialTick) % cycleTicks) / cycleTicks;
 
    VertexConsumer consumer = event.getMultiBufferSource().getBuffer(GHOST_RENDER_TYPE);
    VertexConsumer gradientConsumer =
            new GradientVertexConsumer(consumer, 0.0F, player.getBbHeight(), 0x8800FF, offset);
 
    model.renderToBuffer(poseStack, gradientConsumer,
            event.getPackedLight(), OverlayTexture.NO_OVERLAY, color);
 
    poseStack.popPose();
}
 
/**
 * Faithful copy of PlayerRenderer.setupRotations + LivingEntityRenderer.setupRotations
 * (1.21.1 mojmap). Post fires after vanilla pops these transforms, so we have to
 * re-apply them - but applying the SAME math means zero rotational desync.
 */
private static void setupRotations(Player player, PoseStack poseStack, float bodyYaw, float partialTick) {
    float swimAmount = player.getSwimAmount(partialTick);
    float viewXRot = player.getViewXRot(partialTick);
 
    if (player.isFallFlying()) {
        baseRotations(player, poseStack, bodyYaw, partialTick);
 
        float flyTicks = (float) player.getFallFlyingTicks() + partialTick;
        float ramp = Mth.clamp(flyTicks * flyTicks / 100.0F, 0.0F, 1.0F);
        if (!player.isAutoSpinAttack()) {
            poseStack.mulPose(Axis.XP.rotationDegrees(ramp * (-90.0F - viewXRot)));
        }
 
        // elytra banking
        Vec3 view = player.getViewVector(partialTick);
        Vec3 motion = player.getDeltaMovement();
        double motionH = motion.horizontalDistanceSqr();
        double viewH = view.horizontalDistanceSqr();
        if (motionH > 0.0 && viewH > 0.0) {
            double dot = (motion.x * view.x + motion.z * view.z) / Math.sqrt(motionH * viewH);
            double cross = motion.x * view.z - motion.z * view.x;
            poseStack.mulPose(Axis.YP.rotation((float) (Math.signum(cross) * Math.acos(dot))));
        }
    } else if (swimAmount > 0.0F) {
        baseRotations(player, poseStack, bodyYaw, partialTick);
        float target = player.isInWater() ? -90.0F - viewXRot : -90.0F;
        poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(swimAmount, 0.0F, target)));
        if (player.isVisuallySwimming()) {
            poseStack.translate(0.0F, -1.0F, 0.3F);
        }
    } else {
        baseRotations(player, poseStack, bodyYaw, partialTick);
    }
}
 
/** LivingEntityRenderer.setupRotations equivalent. */
private static void baseRotations(Player player, PoseStack poseStack, float bodyYaw, float partialTick) {
    if (player.isFullyFrozen()) {
        bodyYaw += (float) (Math.cos(player.tickCount * 3.25) * Math.PI * 0.4);
    }
    if (!player.hasPose(Pose.SLEEPING)) {
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - bodyYaw));
    }
    if (player.deathTime > 0) {
        float f = (player.deathTime + partialTick - 1.0F) / 20.0F * 1.6F;
        f = Math.min(Mth.sqrt(f), 1.0F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(f * 90.0F));
    } else if (player.isAutoSpinAttack()) {
        poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F - player.getXRot()));
        poseStack.mulPose(Axis.YP.rotationDegrees((player.tickCount + partialTick) * -75.0F));
    } else if (player.hasPose(Pose.SLEEPING)) {
        Direction bed = player.getBedOrientation();
        float rot = bed != null ? sleepDirectionToRotation(bed) : bodyYaw;
        poseStack.mulPose(Axis.YP.rotationDegrees(rot));
        poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F)); // getFlipDegrees
        poseStack.mulPose(Axis.YP.rotationDegrees(270.0F));
    } else if (LivingEntityRenderer.isEntityUpsideDown(player)) {
        poseStack.translate(0.0F, player.getBbHeight() + 0.1F, 0.0F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
    }
}
 
private static float sleepDirectionToRotation(Direction direction) {
    return switch (direction) {
        case SOUTH -> 90.0F;
        case WEST -> 0.0F;
        case NORTH -> 270.0F;
        case EAST -> 180.0F;
        default -> 0.0F;
    };
}
}
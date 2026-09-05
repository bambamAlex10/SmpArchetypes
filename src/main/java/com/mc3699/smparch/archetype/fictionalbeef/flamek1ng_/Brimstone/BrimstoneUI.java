package com.mc3699.smparch.archetype.fictionalbeef.flamek1ng_.Brimstone;

import com.mc3699.smparch.archetype.fictionalbeef.flamek1ng_.FlameK1ng_Sounds;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import org.joml.Matrix4f;

import java.util.UUID;

@EventBusSubscriber(modid = "smparch", value = Dist.CLIENT)
public class BrimstoneUI {
    // ---- textures ----
    // Animation sheet: all frames stacked VERTICALLY in one PNG.
    private static final ResourceLocation ANIM_SHEET = ResourceLocation.fromNamespaceAndPath("smparch",
            "textures/misc/coinflipani.png");

    private static final ResourceLocation FINAL_IMAGE_HEADS = ResourceLocation.fromNamespaceAndPath("smparch",
            "textures/misc/coinflipheads.png");
    private static final ResourceLocation FINAL_IMAGE_TAILS = ResourceLocation.fromNamespaceAndPath("smparch",
            "textures/misc/coinfliptails.png");

    // ---- timing config (all in ticks, 20/sec) ----
    private static final int FRAME_COUNT = 8;
    private static final float TICKS_PER_FRAME = 5.0f;
    private static final float HOLD_TICKS = 40.0f;
    private static final float FADE_TICKS = 20.0f;

    // ---- visuals ----
    private static final float SIZE = 0.5f;
    private static final float Y_OFFSET = 0.6f;
    private static final boolean FULL_BRIGHT = true;
    private static final boolean SMOOTH_BLEND = true; // smoothstep easing on the crossfade

    // ---- texture pixel sizes (must match your PNGs) ----
    private static final int SHEET_FRAME_PX = 16;   // one frame in the sheet is 64x64
    private static final int RESULT_PX = 16;        // heads/tails images are 64x64
    // ---- state ----
    private static UUID targetPlayer = null;
    private static double startTime = Double.NaN;
    private static boolean heads;
    private static boolean resultSoundPlayed;
    private static boolean startingSoundPlayed;

    // ---- HUD visuals ----
    private static final int HUD_SIZE = 64;          // on-screen size in GUI pixels
    private static final float HUD_Y_FRACTION = 0.72f; // vertical center as fraction of screen height
    private static final float POP_TICKS = 5.0f;     // result bump-settle duration
    private static final float POP_AMOUNT = 0.28f;   // overshoot scale on reveal

    public static void play(UUID playerId, boolean result) {
        targetPlayer = playerId;
        startTime = Double.NaN;
        heads = result;
        resultSoundPlayed = false;
    }
 
    public static void stop() {
        targetPlayer = null;
        startTime = Double.NaN;
    }
 
    // =========================================================================
    // Shared animation sampling - single source of truth for BOTH renderers
    // =========================================================================
 
    /**
     * @param baseFrame sheet frame index, or -1 for the result image
     * @param topFrame  crossfade layer: sheet frame index, -1 for result image, -2 for none
     * @param topBlend  0..1 opacity of the top layer
     * @param alpha     0..1 global opacity (drops during fade-out)
     * @param holdTime  ticks since the result locked in, or -1 before that
     */
    private record Sample(int baseFrame, int topFrame, float topBlend, float alpha, float holdTime) {}
 
    /** Returns null when the whole sequence has finished. */
    private static Sample sample(double elapsed) {
        double animLength = FRAME_COUNT * TICKS_PER_FRAME;
 
        if (elapsed < animLength) {
            double frameProgress = elapsed / TICKS_PER_FRAME;
            int frame = Math.min((int) frameProgress, FRAME_COUNT - 1);
            float blend = (float) (frameProgress - frame);
            if (SMOOTH_BLEND) {
                blend = blend * blend * (3.0f - 2.0f * blend);
            }
            int top = (frame < FRAME_COUNT - 1) ? frame + 1 : -1; // last frame melts into the result
            return new Sample(frame, top, blend, 1.0f, -1.0f);
        }
 
        if (elapsed < animLength + HOLD_TICKS) {
            return new Sample(-1, -2, 0.0f, 1.0f, (float) (elapsed - animLength));
        }
 
        if (elapsed < animLength + HOLD_TICKS + FADE_TICKS) {
            float t = (float) ((elapsed - animLength - HOLD_TICKS) / FADE_TICKS);
            t = t * t * (3.0f - 2.0f * t);
            return new Sample(-1, -2, 0.0f, 1.0f - t, (float) (elapsed - animLength));
        }
 
        return null;
    }
 
    private static double elapsedNow(Minecraft mc, float partialTick) {
        double now = mc.level.getGameTime() + partialTick;
        if (Double.isNaN(startTime)) {
            startTime = now;
        }
        return now - startTime;
    }
 
    private static void maybePlayResultSound(Minecraft mc, Sample s, double x, double y, double z) {


            if (s.holdTime() >= 0.0f && !resultSoundPlayed) {
                resultSoundPlayed = true;
                if (heads) {
                    mc.level.playLocalSound(x, y, z,
                            FlameK1ng_Sounds.COINFLIPHEADS.value(), SoundSource.PLAYERS,
                            1f, 1.0f, false);
                } else {
                    mc.level.playLocalSound(x, y, z,
                            FlameK1ng_Sounds.COINFLIPTAILS.value(), SoundSource.PLAYERS,
                            1f, 0.4f, false);

                }
            }
    }
 
    private static ResourceLocation resultTex() {
        return heads ? FINAL_IMAGE_HEADS : FINAL_IMAGE_TAILS;
    }
 
    // =========================================================================
    // World renderer - billboard above the target player's head
    // =========================================================================
 
    @SubscribeEvent
    public static void onRenderPlayer(RenderPlayerEvent.Post event) {
        if (targetPlayer == null) return;
 
        Player player = event.getEntity();
        if (!player.getUUID().equals(targetPlayer)) return;
 
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
 
        // Optional: skip the overhead coin for the local player, since they
        // get the HUD version. Delete these two lines to show both.
        if (player == mc.player) return;
 
        if (!startingSoundPlayed) {
            startingSoundPlayed = true;
            mc.level.playLocalSound(player.getX(), player.getY(), player.getZ(),
                    FlameK1ng_Sounds.STARTCOINFLIP.value(), SoundSource.PLAYERS,
                    1f, 1.0f, false);
        }
        Sample s = sample(elapsedNow(mc, event.getPartialTick()));
        if (s == null) {
            stop();
            return;
        }
        maybePlayResultSound(mc, s, player.getX(), player.getY(), player.getZ());
 
        PoseStack pose = event.getPoseStack();
        pose.pushPose();
        pose.translate(0.0, player.getBbHeight() + Y_OFFSET, 0.0);
        pose.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());
        pose.scale(SIZE, -SIZE, SIZE);
 
        int light = FULL_BRIGHT ? LightTexture.FULL_BRIGHT : event.getPackedLight();
        int baseA = (int) (s.alpha() * 255.0f);
 
        drawWorldQuad(pose, event.getMultiBufferSource(), s.baseFrame(), baseA, light);
        if (s.topFrame() != -2 && s.topBlend() > 0.0f) {
            drawWorldQuad(pose, event.getMultiBufferSource(), s.topFrame(),
                    (int) (s.topBlend() * s.alpha() * 255.0f), light);
        }
 
        pose.popPose();
    }
 
    private static void drawWorldQuad(PoseStack pose, MultiBufferSource buffers,
                                      int frame, int alpha, int light) {
        ResourceLocation tex;
        float v0, v1;
        if (frame == -1) {
            tex = resultTex();
            v0 = 0.0f;
            v1 = 1.0f;
        } else {
            tex = ANIM_SHEET;
            v0 = frame / (float) FRAME_COUNT;
            v1 = (frame + 1) / (float) FRAME_COUNT;
        }
 
        VertexConsumer vc = buffers.getBuffer(RenderType.text(tex));
        Matrix4f mat = pose.last().pose();
        float h = 0.5f;
        vc.addVertex(mat, -h, -h, 0.0f).setColor(255, 255, 255, alpha).setUv(0.0f, v0).setLight(light);
        vc.addVertex(mat, -h,  h, 0.0f).setColor(255, 255, 255, alpha).setUv(0.0f, v1).setLight(light);
        vc.addVertex(mat,  h,  h, 0.0f).setColor(255, 255, 255, alpha).setUv(1.0f, v1).setLight(light);
        vc.addVertex(mat,  h, -h, 0.0f).setColor(255, 255, 255, alpha).setUv(1.0f, v0).setLight(light);
    }
 
    // =========================================================================
    // HUD renderer - only when the LOCAL player is the one being flipped
    // =========================================================================
 
    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        if (targetPlayer == null) return;
 
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        if (!mc.player.getUUID().equals(targetPlayer)) return;
        if (mc.options.hideGui) return;
 
        if (!startingSoundPlayed) {
            startingSoundPlayed = true;
            mc.level.playLocalSound(mc.player.getX(), mc.player.getY(), mc.player.getZ(),
                    FlameK1ng_Sounds.STARTCOINFLIP.value(), SoundSource.PLAYERS,
                    1f, 1.0f, false);
        }
        // DeltaTracker -> plain partial-tick float
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
 
        Sample s = sample(elapsedNow(mc, partialTick));
        if (s == null) {
            stop();
            return;
        }
        maybePlayResultSound(mc, s, mc.player.getX(), mc.player.getY(), mc.player.getZ());
 
        GuiGraphics gui = event.getGuiGraphics();
        int cx = gui.guiWidth() / 2;
        int cy = (int) (gui.guiHeight() * HUD_Y_FRACTION);
        int x = cx - HUD_SIZE / 2;
        int y = cy - HUD_SIZE / 2;
 
        // Bump-settle: overshoot scale when the result locks in, easing back to 1.
        float scale = 1.0f;
        if (s.holdTime() >= 0.0f && s.holdTime() < POP_TICKS) {
            float t = s.holdTime() / POP_TICKS;
            t = t * t * (3.0f - 2.0f * t);
            scale = 1.0f + POP_AMOUNT * (1.0f - t);
        }
 
        PoseStack pose = gui.pose();
        pose.pushPose();
        pose.translate(cx, cy, 0);
        pose.scale(scale, scale, 1.0f);
        pose.translate(-cx, -cy, 0);
 
        RenderSystem.enableBlend();
 
        gui.setColor(1.0f, 1.0f, 1.0f, s.alpha());
        blitFrame(gui, s.baseFrame(), x, y);
 
        if (s.topFrame() != -2 && s.topBlend() > 0.0f) {
            gui.setColor(1.0f, 1.0f, 1.0f, s.topBlend() * s.alpha());
            blitFrame(gui, s.topFrame(), x, y);
        }
 
        gui.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
        pose.popPose();
    }
 
    private static void blitFrame(GuiGraphics gui, int frame, int x, int y) {
        if (frame == -1) {
            gui.blit(resultTex(), x, y, HUD_SIZE, HUD_SIZE,
                    0.0f, 0.0f, RESULT_PX, RESULT_PX, RESULT_PX, RESULT_PX);
        } else {
            gui.blit(ANIM_SHEET, x, y, HUD_SIZE, HUD_SIZE,
                    0.0f, frame * SHEET_FRAME_PX,
                    SHEET_FRAME_PX, SHEET_FRAME_PX,
                    SHEET_FRAME_PX, SHEET_FRAME_PX * FRAME_COUNT);
        }
    }
}
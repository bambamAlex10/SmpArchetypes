package com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidStep.VoidStepUI;

import org.joml.Matrix4f;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.YkorioAttachments;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidStep.VoidStepHandlers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(modid = SMPArch.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class VoidStepUI {

    @SubscribeEvent
public static void onLevelRender(RenderLevelStageEvent event) {

    if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;

    Minecraft mc = Minecraft.getInstance();
    Player player = mc.player;
    if (player == null || !player.hasData(YkorioAttachments.VOID_STEP)) return;

    Level level = player.level();
    int radius = (int) VoidStepHandlers.getRadius(player);
    int radiusSq = radius * radius;
    BlockPos playerPos = player.blockPosition();

    PoseStack poseStack = event.getPoseStack();
    Vec3 cam = event.getCamera().getPosition();
    MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
    VertexConsumer vc = bufferSource.getBuffer(VoidStepRenderTypes.HIGHLIGHT_FILL);

    poseStack.pushPose();
    poseStack.translate(-cam.x, -cam.y, -cam.z);
    Matrix4f matrix = poseStack.last().pose();

    float r = 0.25F, g = 0.0F, b = 0.25F, a = 0.6F;
    BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

    for (int x = -radius; x <= radius; x++) {
        for (int z = -radius; z <= radius; z++) {
            int xzDistSq = x * x + z * z;
            if (xzDistSq > radiusSq) continue;
            
            // Calculate max vertical offset based on horizontal distance
            int maxYOffset = (int) Math.sqrt(radiusSq - xzDistSq);
            
            for (int y = -maxYOffset; y <= maxYOffset; y++) {
                pos.set(playerPos.getX() + x, playerPos.getY() + y, playerPos.getZ() + z);
                if (level.isOutsideBuildHeight(pos)) continue;
                if (!level.getBlockState(pos).isFaceSturdy(level, pos, Direction.UP)) continue;

                float x0 = pos.getX(),     y0 = pos.getY(),     z0 = pos.getZ();
                float x1 = pos.getX() + 1, y1 = pos.getY() + 1, z1 = pos.getZ() + 1;

                quad(vc, matrix, x0,y0,z0, x1,y0,z0, x1,y0,z1, x0,y0,z1, r,g,b,a); // down
                quad(vc, matrix, x0,y1,z0, x0,y1,z1, x1,y1,z1, x1,y1,z0, r,g,b,a); // up
                quad(vc, matrix, x0,y0,z0, x0,y1,z0, x1,y1,z0, x1,y0,z0, r,g,b,a); // north
                quad(vc, matrix, x0,y0,z1, x1,y0,z1, x1,y1,z1, x0,y1,z1, r,g,b,a); // south
                quad(vc, matrix, x0,y0,z0, x0,y0,z1, x0,y1,z1, x0,y1,z0, r,g,b,a); // west
                quad(vc, matrix, x1,y0,z0, x1,y1,z0, x1,y1,z1, x1,y0,z1, r,g,b,a); // east
            }
        }
    }

    poseStack.popPose();
    bufferSource.endBatch(VoidStepRenderTypes.HIGHLIGHT_FILL);
}

private static void quad(VertexConsumer vc, Matrix4f m,
        float ax, float ay, float az, float bx, float by, float bz,
        float cx, float cy, float cz, float dx, float dy, float dz,
        float r, float g, float b, float a) {
    vc.addVertex(m, ax, ay, az).setColor(r, g, b, a);
    vc.addVertex(m, bx, by, bz).setColor(r, g, b, a);
    vc.addVertex(m, cx, cy, cz).setColor(r, g, b, a);
    vc.addVertex(m, dx, dy, dz).setColor(r, g, b, a);
}
}
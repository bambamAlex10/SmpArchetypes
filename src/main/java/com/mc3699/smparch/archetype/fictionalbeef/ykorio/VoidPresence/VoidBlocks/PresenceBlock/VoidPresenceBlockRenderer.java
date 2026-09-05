package com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidPresence.VoidBlocks.PresenceBlock;

import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.YkorioAttachments;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.YkorioSounds;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidPresence.VoidPresenceHandlers;
import com.mc3699.smparch.registry.SMPBlocks;
import com.mc3699.smparch.registry.SMPSounds;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexSorting;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance.Attenuation;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.api.distmarker.Dist;

@EventBusSubscriber(modid = SMPArch.MODID, value = Dist.CLIENT)
public class VoidPresenceBlockRenderer {
    private static SoundInstance currentFogSound = null;

    private static void updateFogSound(Player player, boolean inFog) {
        SoundManager soundManager = Minecraft.getInstance().getSoundManager();

        if (inFog) {
            if (currentFogSound == null || !soundManager.isActive(currentFogSound)) {
                // Use a custom looping sound; replace with your own SoundEvent
                SoundEvent loopEvent = YkorioSounds.SMOKETHEMESONG.value(); // or SoundEvents.AMBIENT_CAVE
                currentFogSound = new SimpleSoundInstance(
                        loopEvent, // SoundEvent
                        SoundSource.AMBIENT, // SoundSource / SoundCategory
                        1.0F, // volume
                        1.0F, // pitch
                        SoundInstance.createUnseededRandom(), // random (Minecraftのバージョンによって必要な場合あり)
                        0, // delay
                        0.0D, 0.0D) {
                    @Override
                    public boolean isLooping() {
                        return true;
                    }
                };
                soundManager.play(currentFogSound);
            }
        } else {
            // Stop the sound if it's playing
            if (currentFogSound != null) {
                soundManager.stop(currentFogSound);
                currentFogSound = null;
            }
        }
    }

    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        Entity e = (event.getCamera().getEntity());

        if (!(e instanceof Player player)) {
            return;
        }

        if (VoidPresenceHandlers.isEntityIntersectingTargetBlock(player, player.level(), SMPBlocks.VOIDBLOCK.get(),
                SMPBlocks.VOIDPRESENCEBLOCK.get())) {

            updateFogSound(player, true);
            if (!event.getCamera().getEntity().hasData(YkorioAttachments.IMMUNITY)) {
                event.setNearPlaneDistance(0.0F);
                event.setFarPlaneDistance(13.0F);
                event.setCanceled(true);
            }
        } else {
            updateFogSound(player, false);

        }
    }

    @SubscribeEvent
    public static void onFogColor(ViewportEvent.ComputeFogColor event) {
        Entity e = (event.getCamera().getEntity());

        if (!(e instanceof Player player)) {
            return;
        }

        if (VoidPresenceHandlers.isEntityIntersectingTargetBlock(player, player.level(), SMPBlocks.VOIDBLOCK.get(),
                SMPBlocks.VOIDPRESENCEBLOCK.get())
                && !event.getCamera().getEntity().hasData(YkorioAttachments.IMMUNITY)) {
            // match the sky colour exactly
            event.setRed(0.1F);
            event.setGreen(0F);
            event.setBlue(0.1F);
        }
    }

}
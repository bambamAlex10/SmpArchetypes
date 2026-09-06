package com.mc3699.smparch.archetype.fictionalbeef.alivealex.Salvation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.archetype.fictionalbeef.alivealex.AliveAlexAttachments;
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
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.api.distmarker.Dist;

public class SalvationUIEveryone {
    public static final Random rand = new Random();

    private static final ResourceLocation SALVATIONUI = ResourceLocation.fromNamespaceAndPath(SMPArch.MODID,
            "textures/gui/hassalvation.png");
    public static final LayeredDraw.Layer HUD_LAYER = (graphics, deltaTracker) -> {
        if (Minecraft.getInstance().level == null) {
            return;
        }
        if (!Minecraft.getInstance().player.hasData(AliveAlexAttachments.SALVATION) && !Minecraft.getInstance().player.hasData(AliveAlexAttachments.DAMAGE_LOADING)) {
            return;
        }

        Window window = Minecraft.getInstance().getWindow();
        int screenWidth = window.getGuiScaledWidth();
        int screenHeight = window.getGuiScaledHeight();

        int textureWidth = 64; 
        int textureHeight = 64;

        int maxSafeWidth = screenWidth - textureWidth;
        int maxSafeHeight = screenHeight - textureHeight;

        rand.setSeed(((Minecraft.getInstance().level.getGameTime()*123456789L)+505));
        int x = (int) (maxSafeWidth * (Minecraft.getInstance().player.hasData(AliveAlexAttachments.DAMAGE_LOADING) ? rand.nextFloat() : .9f));
        rand.setSeed(((Minecraft.getInstance().level.getGameTime()*987654321L)+105));
        int y = (int) (maxSafeHeight * (Minecraft.getInstance().player.hasData(AliveAlexAttachments.DAMAGE_LOADING) ? rand.nextFloat() : .9f));

        graphics.blit(SALVATIONUI, x, y, 0, 0, textureWidth, textureHeight, 64, 64);
    };
}

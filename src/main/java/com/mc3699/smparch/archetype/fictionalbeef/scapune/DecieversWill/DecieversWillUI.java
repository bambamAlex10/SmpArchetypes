package com.mc3699.smparch.archetype.fictionalbeef.scapune.DecieversWill;

import com.mc3699.smparch.archetype.fictionalbeef.scapune.ScapuneAttachments;
import com.mc3699.smparch.archetype.fictionalbeef.scapune.DecieversGrace.DecieversGhost.DecieversGhostEntity;
import com.mc3699.smparch.registry.SMPAttachments;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;

import java.util.Optional;

import org.joml.Matrix4f;

@EventBusSubscriber(modid = "smparch", value = Dist.CLIENT)
public class DecieversWillUI {

    @SubscribeEvent
    public static void onRenderGui(RenderGuiLayerEvent.Post event) {
    }

}
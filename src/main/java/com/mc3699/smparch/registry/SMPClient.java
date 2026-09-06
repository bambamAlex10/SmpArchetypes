package com.mc3699.smparch.registry;

import javax.swing.Renderer;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.archetype.fictionalbeef.alivealex.AliveAlexSounds;
import com.mc3699.smparch.archetype.fictionalbeef.alivealex.Salvation.SalvationUIEveryone;
import com.mc3699.smparch.archetype.fictionalbeef.alivealex.Salvation.SalvationUIOwner;
import com.mc3699.smparch.archetype.fictionalbeef.bigmanrake.Concerto.ConcertoUI;
import com.mc3699.smparch.archetype.fictionalbeef.jkmc.ExplosionPassive.ExplosvePassiveUI;
import com.mc3699.smparch.archetype.fictionalbeef.scapune.DecieversGrace.DecieversGhost.DecieversGhostClient;
import com.mc3699.smparch.archetype.fictionalbeef.scapune.DecieversGrace.DecieversGhost.DecieversGhostEntity;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidPresence.VoidBlocks.PresenceBlock.VoidPresenceBlockEntity;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidStep.VoidEcho.VoidEchoClient;
import com.mc3699.smparch.packets.displayTargetDamage;
import com.mc3699.smparch.util.CPMPlugin.CPMRenderTypeFunction;
import com.mc3699.smparch.util.EntityHitbox.EntityHitboxClient;
import com.mc3699.smparch.util.CPMPlugin.CPMReflectionHelper;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.logging.LogUtils;

import net.mc3699.provenance.Provenance;
import net.mc3699.provenance.ProvenanceDataHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderType.CompositeState;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = "smparch", value = Dist.CLIENT)
public class SMPClient {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        CPMReflectionHelper.initialize();
        event.enqueueWork(() -> {
            // Sets the render type to translucent (like water/stained glass)
            ItemBlockRenderTypes.setRenderLayer(SMPBlocks.VOIDBLOCK.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(SMPBlocks.VOIDPRESENCEBLOCK.get(), RenderType.translucent());
        });
    }

    // @SubscribeEvent
    // public static void onRenderGui(RenderGuiEvent.Post event) {
    // RetributionUI.onRenderGui(event);
    // }

    @SubscribeEvent
    public static void onRegisterProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(SMPParticles.NOTES.get(), SMPParticles.noteParticle.Provider::new);
        event.registerSpriteSet(SMPParticles.QUEENBEE.get(), SMPParticles.queenbeeParticle.Provider::new);
        event.registerSpriteSet(SMPParticles.FIREDASH.get(), SMPParticles.firedashParticle.Provider::new);
        event.registerSpriteSet(SMPParticles.MAJIK.get(), SMPParticles.majikParticles.Provider::new);
        event.registerSpriteSet(SMPParticles.VIBRATO.get(), SMPParticles.VibratoParticle.Provider::new);
    }

    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        PlayerRenderer renderer1 = event.getSkin(PlayerSkin.Model.WIDE);
        PlayerRenderer renderer2 = event.getSkin(PlayerSkin.Model.SLIM);

        if (renderer1 != null) {
            renderer1.addLayer(new ConcertoUI(renderer1));
        }

        if (renderer2 != null) {
            renderer2.addLayer(new ConcertoUI(renderer2));
        }
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().level != null) {
            SalvationUIOwner.tick();
        }
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(SMPEntities.DECIEVERSGHOST.get(), DecieversGhostClient::new);
        event.registerEntityRenderer(SMPEntities.VOIDECHO.get(), VoidEchoClient::new);
        event.registerEntityRenderer(SMPEntities.ENTITYHITBOX.get(), EntityHitboxClient::new);
    }

    @SubscribeEvent
    public static void onRegisterLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(ResourceLocation.fromNamespaceAndPath(SMPArch.MODID, "movement1text"),
                ConcertoUI.Movement1UI);
        event.registerAboveAll(
                ResourceLocation.fromNamespaceAndPath(SMPArch.MODID, "salvationui"), SalvationUIEveryone.HUD_LAYER);
        event.registerAboveAll(
                ResourceLocation.fromNamespaceAndPath(SMPArch.MODID, "jkmcui"), ExplosvePassiveUI.HUD_LAYER);
    }
}

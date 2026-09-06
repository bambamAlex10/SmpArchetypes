package com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidStep.VoidEcho;

import com.mc3699.smparch.archetype.fictionalbeef.scapune.DecieversGrace.DecieversGhost.DecieversGhostEntity;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidStep.VoidStepAbility;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidStep.VoidStepHandlers;
import com.mc3699.smparch.util.CPMPlugin.CPMReflectionHelper;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.pipeline.VertexConsumerWrapper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class VoidEchoClient extends LivingEntityRenderer<VoidEchoEntity, PlayerModel<VoidEchoEntity>> {
    // Solid black texture – works with any UV mapping
    private static final ResourceLocation BLACK_TEXTURE = ResourceLocation.fromNamespaceAndPath("minecraft",
            "textures/block/black_concrete.png");

    public VoidEchoClient(EntityRendererProvider.Context context) {
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.0f);
        CPMReflectionHelper.initialize();

        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    private static final float MIN_ALPHA = 0.0f;
    private static final float MAX_ALPHA = 1.0f;

    private float getAlpha(VoidEchoEntity entity) {
        int lifeTicks = entity.getLifeTicks();
        if (lifeTicks <= 0)
            return MAX_ALPHA;

        float alpha = (float) lifeTicks / (float) VoidStepAbility.GhostDuration;
        return Math.max(MIN_ALPHA, Math.min(MAX_ALPHA, alpha));
    }

    @Nullable
    private GameProfile getGameProfileForOwner(VoidEchoEntity entity) {
        Optional<UUID> ownerUUID = entity.getOwnerUUID();
        if (ownerUUID.isEmpty())
            return null;

        UUID uuid = ownerUUID.get();
        Minecraft mc = Minecraft.getInstance();

        if (mc.player != null && mc.player.getUUID().equals(uuid)) {
            return mc.player.getGameProfile();
        }

        if (mc.getConnection() != null) {
            PlayerInfo info = mc.getConnection().getPlayerInfo(uuid);
            if (info != null)
                return info.getProfile();
        }

        return new GameProfile(uuid, ""); // will attempt to load skin by UUID
    }

    @Override
    @NotNull
    public ResourceLocation getTextureLocation(VoidEchoEntity entity) {
        // Always return the solid black texture
        return BLACK_TEXTURE;
    }

    private MultiBufferSource wrapForTranslucency(MultiBufferSource original, float alpha) {
        return new MultiBufferSource() {
            @Override
            public VertexConsumer getBuffer(RenderType renderType) {
                // Only replace the entity's main texture render type (and possibly armor/hand
                // layers)
                if (renderType == RenderType.entityCutoutNoCull(BLACK_TEXTURE) ||
                        renderType == RenderType.entityTranslucent(BLACK_TEXTURE)) {

                    RenderType translucent = RenderType.entityTranslucent(BLACK_TEXTURE);
                    VertexConsumer originalConsumer = original.getBuffer(translucent);
                    // Apply alpha to every vertex
                    return new VertexConsumerWrapper(originalConsumer) {
                        @Override
                        public VertexConsumer setColor(int r, int g, int b, int a) {
                            // Override alpha with our fading value
                            int finalAlpha = Math.round(alpha * 255);
                            return super.setColor(r, g, b, finalAlpha);
                        }
                    };
                }
                // For other layers (e.g., item in hand) – optionally also fade them
                return original.getBuffer(renderType);
            }
        };
    }

@Override
public void render(VoidEchoEntity entity, float entityYaw, float partialTicks,
        PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
 
    this.model.crouching = entity.isVisualCrouching();
 
    float alpha = getAlpha(entity);
    MultiBufferSource translucentBuffer = wrapForTranslucency(buffer, alpha);
 
    // Isolated stack: a throw mid-render can corrupt this copy, never the frame's
    // real stack ("Pose stack not empty" protection).
    PoseStack cpmStack = new PoseStack();
    cpmStack.last().pose().mul(poseStack.last().pose());
    cpmStack.last().normal().mul(poseStack.last().normal());
 
    GameProfile profile = getGameProfileForOwner(entity);
 
    boolean attempted = profile != null && CPMReflectionHelper.renderWithCPM(
            profile, this.model, translucentBuffer,
            () -> super.render(entity, entityYaw, partialTicks, cpmStack, translucentBuffer, packedLight));
 
    if (!attempted) {
        // CPM never engaged this frame (not ready, or no profile): vanilla fallback.
        super.render(entity, entityYaw, partialTicks, cpmStack, translucentBuffer, packedLight);
    }
    // If attempted-but-threw: helper logged the real cause; we skip the fallback so we
    // don't emit the model twice in one frame with half-bound CPM state.
}


    // @Override
    // public void render(VoidEchoEntity entity, float entityYaw, float partialTicks,
    //                    PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
    //     this.model.crouching = entity.isVisualCrouching();

    //     GameProfile profile = getGameProfileForOwner(entity);
    //     if (profile == null) {
    //         super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    //         return;
    //     }

    //     Object renderer = CPMReflectionHelper.getRenderer();
    //     if (renderer != null && CPMReflectionHelper.isApiReady()) {
    //         try {
    //             // Prepare CPM – this applies the custom model shape but ignores its texture
    //             CPMReflectionHelper.getSetGameProfile().invoke(renderer, profile);
    //             CPMReflectionHelper.getSetRenderModel().invoke(renderer, this.model);
    //             CPMReflectionHelper.getPreRender().invoke(renderer, buffer,
    //                     CPMReflectionHelper.getAnimationModePlayer());

    //             // Render the entity – our getTextureLocation() provides the black texture
    //             super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);

    //             // Clean up CPM
    //             CPMReflectionHelper.getPostRender().invoke(renderer);
    //             return;
    //         } catch (Exception e) {
    //             e.printStackTrace();
    //         }
    //     }

    //     // Fallback to pure vanilla if CPM isn't ready
    //     super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    // }
}

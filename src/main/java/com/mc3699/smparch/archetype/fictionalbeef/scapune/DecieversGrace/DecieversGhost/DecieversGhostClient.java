package com.mc3699.smparch.archetype.fictionalbeef.scapune.DecieversGrace.DecieversGhost;

import com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidStep.VoidEcho.VoidEchoEntity;
import com.mc3699.smparch.util.CPMPlugin.CPMReflectionHelper;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.UUID;

public class DecieversGhostClient
        extends LivingEntityRenderer<DecieversGhostEntity, PlayerModel<DecieversGhostEntity>> {
    private static final ResourceLocation STEVE_SKIN = DefaultPlayerSkin.getDefaultTexture();

    private boolean cpmActive = false;
    private ResourceLocation cachedCPMTexture = null;

    public DecieversGhostClient(EntityRendererProvider.Context context) {
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5f);
        CPMReflectionHelper.initialize();
        this.addLayer(new HumanoidArmorLayer<>(this,
                new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
                new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)),
                context.getModelManager()));
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    @Nullable
    private GameProfile getGameProfileForOwner(DecieversGhostEntity entity) {
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

        return new GameProfile(uuid, "");
    }


    @Override
    @NotNull
    public ResourceLocation getTextureLocation(DecieversGhostEntity entity) {
        if (cpmActive && cachedCPMTexture != null) {
            return cachedCPMTexture;
        }

        GameProfile profile = getGameProfileForOwner(entity);
        if (profile != null) {
            ResourceLocation cpmTex = CPMReflectionHelper.getCPMTexture(profile, this.model);
            if (cpmTex != null)
                return cpmTex;
            return Minecraft.getInstance().getSkinManager().getInsecureSkin(profile).texture();
        }
        return STEVE_SKIN;
    }


@Override
public void render(DecieversGhostEntity entity, float entityYaw, float partialTicks,
        PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
 
    this.model.crouching = entity.isVisualCrouching();
 
    PoseStack cpmStack = new PoseStack();
    cpmStack.last().pose().mul(poseStack.last().pose());
    cpmStack.last().normal().mul(poseStack.last().normal());
 
    GameProfile profile = getGameProfileForOwner(entity);
 
    // Resolve the CPM texture up front so getTextureLocation() sees it during the body.
    ResourceLocation tex = profile != null
            ? CPMReflectionHelper.getCPMTexture(profile, this.model)
            : null;
    cachedCPMTexture = tex;
    cpmActive = tex != null;
 
    try {
        boolean attempted = profile != null && CPMReflectionHelper.renderWithCPM(
                profile, this.model, buffer,
                () -> super.render(entity, entityYaw, partialTicks, cpmStack, buffer, packedLight));
 
        if (!attempted) {
            super.render(entity, entityYaw, partialTicks, cpmStack, buffer, packedLight);
        }
    } finally {
        cpmActive = false;
        cachedCPMTexture = null;
    }
}
}
package com.mc3699.smparch.util.EntityHitbox;

import com.mc3699.smparch.registry.SMPArchetypes;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import com.mc3699.smparch.SMPArch;

public class EntityHitboxClient extends EntityRenderer<EntityHitbox> {
    private ResourceLocation TEXTURE = 
        ResourceLocation.fromNamespaceAndPath(SMPArch.MODID, 
            "textures/entity/my_entity.png"

        );

    public EntityHitboxClient(EntityRendererProvider.Context context,ResourceLocation dynamicTexture) {
        super(context);
        TEXTURE = dynamicTexture;
    }
    public EntityHitboxClient(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(EntityHitbox entity) {
        return TEXTURE;
    }
}
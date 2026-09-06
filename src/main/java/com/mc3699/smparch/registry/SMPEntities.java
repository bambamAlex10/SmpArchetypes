package com.mc3699.smparch.registry;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.archetype.fictionalbeef.scapune.DecieversGrace.DecieversGhost.DecieversGhostEntity;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidStep.VoidEcho.VoidEchoEntity;
import com.mc3699.smparch.entity.AbilityProjectile;
import com.mc3699.smparch.util.EntityHitbox.EntityHitbox;
import com.mc3699.smparch.util.EntityHitbox.EntityHitboxClient;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SMPEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, SMPArch.MODID);

    public static final Supplier<EntityType<AbilityProjectile>> ABILITY_PROJECTILE =
            ENTITIES.register("ability_projectile",
                    () -> EntityType.Builder.of(AbilityProjectile::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build("ability_projectile")
            );
    public static final Supplier<EntityType<DecieversGhostEntity>> DECIEVERSGHOST = ENTITIES.register("decievers_ghost",
        () -> EntityType.Builder.of(DecieversGhostEntity::new, MobCategory.MISC)
            .sized(0.6f, 1.9f)
            .eyeHeight(1.74f)  
            .clientTrackingRange(64) 
            .build("decievers_ghost")
    );

    public static final Supplier<EntityType<VoidEchoEntity>> VOIDECHO = ENTITIES.register("void_echo",
        () -> EntityType.Builder.of(VoidEchoEntity::new, MobCategory.MISC)
            .sized(0.6f, 1.9f)
            .eyeHeight(1.74f)  
            .clientTrackingRange(64)
            .build("void_echo")
    );
    public static final DeferredHolder<EntityType<?>, EntityType<EntityHitbox>> ENTITYHITBOX = 
            ENTITIES.register("entity_hitbox", () -> 
                    EntityType.Builder.<EntityHitbox>of(EntityHitbox::new, MobCategory.MISC)
                            .sized(1.0F, 1.0F)
                            .build("entity_hitbox")
            );
            
    public static void register(IEventBus eventBus)
    {
        ENTITIES.register(eventBus);

        eventBus.addListener((EntityAttributeCreationEvent event) -> {
            event.put(
                SMPEntities.DECIEVERSGHOST.get(),
                LivingEntity.createLivingAttributes().build()
            );
            event.put(
                SMPEntities.VOIDECHO.get(),
                LivingEntity.createLivingAttributes().build()
            );
        });

        
    }



}

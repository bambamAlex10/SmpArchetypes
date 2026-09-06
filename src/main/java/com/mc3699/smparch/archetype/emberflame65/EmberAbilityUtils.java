package com.mc3699.smparch.archetype.emberflame65;


import com.mc3699.smparch.SMPArch;
import net.mc3699.provenance.ProvenanceDataHandler;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = SMPArch.MODID)
public class EmberAbilityUtils {
    private static final Map<UUID, Integer> soakedTracker = new HashMap<>();
    private static final Map<UUID, Integer> wetTracker = new HashMap<>();
    private static final Map<UUID, Integer> flightTracker = new HashMap<>();
    private static final int SOAKED_DURATION = 200;
    private static final int WET_DURATION = 600;
    private static final int FLIGHT_DURATION = 3000;

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!ProvenanceDataHandler.getAbilityIds(player).contains(ResourceLocation.fromNamespaceAndPath(SMPArch.MODID, "flight"))) return;
        tickConditions(player);
    }

    @SubscribeEvent
    public static void onFallDamage(LivingFallEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!ProvenanceDataHandler.getAbilityIds(player).contains(ResourceLocation.fromNamespaceAndPath(SMPArch.MODID, "flight"))) return;

        event.setDistance(Math.max(0, event.getDistance() - 3));
        event.setDamageMultiplier(event.getDamageMultiplier() * 0.8f);
    }


    @SubscribeEvent
    public static void onDamage(LivingDamageEvent.Pre event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!ProvenanceDataHandler.getAbilityIds(player).contains(ResourceLocation.fromNamespaceAndPath(SMPArch.MODID, "flight"))) return;
        if (event.getSource().is(DamageTypes.LIGHTNING_BOLT)) {
            event.setNewDamage(0);
            return;
        }

        if (player.getAbilities().mayfly) {
            player.getAbilities().flying = false;
            player.getAbilities().mayfly = false;
            player.onUpdateAbilities();
        }
    }

    @SubscribeEvent
    public static void onPlayerClick(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        ItemStack stack = event.getItemStack();
        if (!stack.is(Tags.Items.SEEDS)) return;
        if (!ProvenanceDataHandler.getAbilityIds(player).contains(ResourceLocation.fromNamespaceAndPath(SMPArch.MODID, "flight"))) return;
        if (player.getFoodData().needsFood()) {
            player.getFoodData().eat(1, 0.0f);
            player.level().playSound(null, player.blockPosition(),
                    SoundEvents.PARROT_EAT, SoundSource.PLAYERS, 1.0f, 1.0f);
            stack.shrink(1);
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        ItemStack item = event.getItemStack();

        if (!item.is(Tags.Items.SEEDS)) return;
        if (!ProvenanceDataHandler.getAbilityIds(player).contains(ResourceLocation.fromNamespaceAndPath(SMPArch.MODID, "flight"))) return;
        if (player.getFoodData().needsFood()) {
            player.getFoodData().eat(2, 0.0f);
            item.shrink(1);
            player.level().playSound(null, player.blockPosition(),
                    SoundEvents.PARROT_EAT, SoundSource.PLAYERS, 1.0f, 1.0f);
            event.setCanceled(true);
        }
    }

    private static void tickConditions(ServerPlayer player) {
        if (!ProvenanceDataHandler.getAbilityIds(player).contains(ResourceLocation.fromNamespaceAndPath(SMPArch.MODID, "flight"))) return;

        UUID id = player.getUUID();
        if (player.isInWater()) {
            soakedTracker.put(id, SOAKED_DURATION);
            wetTracker.remove(id);
        } else if (soakedTracker.getOrDefault(id, 0) > 0) {
            soakedTracker.merge(id, -1, Integer::sum);
            if (soakedTracker.getOrDefault(id, 0) <= 0) {
                soakedTracker.remove(id);
                wetTracker.put(id, WET_DURATION);
            }
        } else if (player.level().isRainingAt(player.getOnPos())) {
            wetTracker.put(id, WET_DURATION);
        } else if (wetTracker.getOrDefault(id, 0) > 0) {
            wetTracker.merge(id, -1, Integer::sum);
            if (wetTracker.getOrDefault(id, 0) <= 0) {
                wetTracker.remove(id);
            }
        }

        if (isSoaked(player) || isWet(player)) {
            spawnWetParticles(player);
        }

        if (flightTracker.getOrDefault(id, 0) > 0) {
            flightTracker.merge(id, -1, Integer::sum);
            System.out.println(flightTracker.get(id));
        }
        if (flightTracker.getOrDefault(id, 0) <= 0) {
            System.out.println("firing");
            flightTracker.remove(id);
            player.getAbilities().flying = false;
            player.getAbilities().mayfly = false;
            player.onUpdateAbilities();
        }

    }

    private static void spawnWetParticles(ServerPlayer player) {
        ServerLevel level = (ServerLevel) player.level();
        RandomSource random = player.getRandom();

        int count = isSoaked(player) ? 4 : 1;

        for (int i = 0; i < count; i++) {
            double x = player.getX() + (random.nextDouble() - 0.5) * 0.85;
            double y = player.getY() + random.nextDouble() * 1.85;
            double z = player.getZ() + (random.nextDouble() - 0.5) * 0.85;
            level.sendParticles(ParticleTypes.FALLING_DRIPSTONE_WATER, x, y, z, 1, 0, 0, 0, 0);
        }
    }

    public static boolean isSoaked(ServerPlayer player) {
        return player.isInWater() || soakedTracker.getOrDefault(player.getUUID(), 0) > 0;
    }

    public static boolean isWet(ServerPlayer player) {
        return isSoaked(player) || player.level().isRainingAt(player.getOnPos()) || wetTracker.getOrDefault(player.getUUID(), 0) > 0;
    }

    public static void activateFlying(ServerPlayer player) {
        flightTracker.put(player.getUUID(), FLIGHT_DURATION);
    }

    public static boolean isFlying(ServerPlayer player) {
        return flightTracker.getOrDefault(player.getUUID(), 0) > 0;
    }

}

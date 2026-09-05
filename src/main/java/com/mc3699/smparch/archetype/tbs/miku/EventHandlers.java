package com.mc3699.smparch.archetype.tbs.miku;

import com.mc3699.smparch.SMPArch;
import net.mc3699.provenance.ProvenanceDataHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = SMPArch.MODID)
public class EventHandlers {
    @SubscribeEvent
    public static void onAttack(AttackEntityEvent event) {
        Player player = event.getEntity();
        if(ProvenanceDataHandler.getAbilities(player).stream().noneMatch(ability -> ability instanceof WardenStrengthAbility)) return;
        
        Entity target = event.getTarget();
        
        var bonus = WardenStrengthAbility.NEXT_ATTACK_BONUS.get(player);
        if (bonus == null) return;
        double baseDamage = player.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
        
        event.setCanceled(true);
        
        target.hurt(player.damageSources().playerAttack(player), (float) (baseDamage + bonus));
        player.level().playSound(null, player.blockPosition(), SoundEvents.WARDEN_ATTACK_IMPACT, SoundSource.PLAYERS, 1.0F, 1.0F);
        player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_ATTACK_STRONG, SoundSource.PLAYERS, 1.0F, 1.0F);
        player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_ATTACK_KNOCKBACK, SoundSource.PLAYERS, 1.0F, 1.0F);
        WardenStrengthAbility.NEXT_ATTACK_BONUS.remove(player);
        player.displayClientMessage(Component.literal("Warden Strength has worn off. (Used on " + event.getTarget().getName().getString() + ")").withStyle(ChatFormatting.ITALIC, ChatFormatting.AQUA), true);
    }
    
    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getPlacedBlock().getBlock().equals(Blocks.SCULK_SHRIEKER))) return;
        if (!(event.getEntity() instanceof Player player)) return;
        if(ProvenanceDataHandler.getAbilities(player).stream().noneMatch(ability -> ability instanceof AidFromBelowAbility)) return;
        event.getLevel().setBlock(event.getPos(),event.getPlacedBlock().setValue(BlockStateProperties.CAN_SUMMON,true),0);
    }
    
    @SubscribeEvent
    public static void onWardenTargeting(LivingChangeTargetEvent event) {
        if (!(event.getEntity() instanceof Warden warden)) return;
        
        boolean hasNearbyWandHolder = warden.level().getEntitiesOfClass(Player.class, warden.getBoundingBox().inflate(16.0))
            .stream()
            .anyMatch(EventHandlers::hasActiveAidFromBelow);
        
        if (hasNearbyWandHolder) {
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent
    public static void onWardenTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Warden warden)) return;
        
        if (warden.getPersistentData().getBoolean("IsFriendly")) return;
        
        if (warden.tickCount % 20 == 0) {
            Player holder = warden.level()
                .getEntitiesOfClass(Player.class, warden.getBoundingBox().inflate(16.0))
                .stream()
                .filter(EventHandlers::hasActiveAidFromBelow)
                .findFirst()
                .orElse(null);
            
            if (holder != null) {
                if (warden.getTarget() == holder) {
                    warden.setTarget(null);
                    warden.getNavigation().stop();
                }
                try {
                    warden.clearAnger(holder);
                } catch (Exception ignored) {}
                warden.getPersistentData().putLong("PeacefulUntil", warden.level().getGameTime() + 100);
            }
        }
        
        long peacefulUntil = warden.getPersistentData().getLong("PeacefulUntil");
        if (peacefulUntil > 0 && warden.level().getGameTime() > peacefulUntil) {
            warden.getPersistentData().remove("PeacefulUntil");
        }
    }
    
    private static boolean hasActiveAidFromBelow(Player player) {
        return ProvenanceDataHandler.getAbilities(player).stream().anyMatch(ability -> ability instanceof AidFromBelowAbility);
    }
    @SubscribeEvent
    public static void onWardenDrops(LivingDropsEvent event) {
        if (event.getEntity() instanceof Warden warden && warden.getPersistentData().getBoolean("NoDrops")) {
            event.getDrops().clear();
        }
    }
    
    @SubscribeEvent
    public static void onWardenXP(LivingExperienceDropEvent event) {
        if (event.getEntity() instanceof Warden warden && warden.getPersistentData().getBoolean("NoDrops")) {
            event.setCanceled(true);
        }
    }
}

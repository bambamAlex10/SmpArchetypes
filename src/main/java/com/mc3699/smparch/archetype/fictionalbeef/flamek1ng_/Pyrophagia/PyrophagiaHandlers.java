package com.mc3699.smparch.archetype.fictionalbeef.flamek1ng_.Pyrophagia;

import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.List;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.archetype.fictionalbeef.flamek1ng_.FlameK1ng_Attachments;
import com.mc3699.smparch.archetype.fictionalbeef.flamek1ng_.PyrophagiaPassive;
import com.mc3699.smparch.registry.SMPAbilities;
import com.mojang.logging.LogUtils;

import net.mc3699.provenance.Provenance;
import net.mc3699.provenance.ProvenanceDataHandler;
import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags.DamageTypes;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = SMPArch.MODID)
public class PyrophagiaHandlers {

    @SubscribeEvent
    public static void onEntityDamage(LivingDamageEvent.Pre Event) {
        Entity sourceAttacker = Event.getSource().getEntity();
        LivingEntity gettingAttacked = Event.getEntity();

        DamageSource container = Event.getContainer().getSource();
        Player cause;

        if (!(sourceAttacker instanceof Player awesome)) {
            if ((container.is(net.minecraft.world.damagesource.DamageTypes.IN_FIRE)
                    || container.is(net.minecraft.world.damagesource.DamageTypes.ON_FIRE)
                    || container.is(net.minecraft.world.damagesource.DamageTypes.LAVA))
                ){
                cause = gettingAttacked.level().getNearestPlayer(gettingAttacked, 10);
            } else {
                cause = null;
            }
        } else {
            if (ProvenanceDataHandler.getAmbientAbilities(awesome).stream().noneMatch(ability -> ability instanceof PyrophagiaPassive)) {
                return;
            }
            cause = awesome;
        }


        if (!(cause instanceof Player)) {
            return;
        }

        boolean isOn = ProvenanceDataHandler.isAbilityEnabled(cause,ResourceLocation.tryParse("smparch:pyrophagia"));//ProvenanceDataHandler.isAbilityEnabled(awesome, null) ; // P

        LogUtils.getLogger().debug("Ability is currently: "+isOn);

        if (!isOn) {
            return;
        }

        LogUtils.getLogger().debug("It's working");

        float currentHealth = cause.getHealth();
        float currentHunger = cause.getFoodData().getFoodLevel();
        float currentSaturation = cause.getFoodData().getSaturationLevel();

        float minimum = Math.min(Math.min(currentHealth, currentSaturation),currentHunger) ;

        if (minimum == currentHealth) {
            cause.heal(.5f);
        } else if (minimum == currentHunger) {
            cause.getFoodData().setFoodLevel(Math.clamp(((int) currentHunger)+1,0,20));
        } else {
            cause.getFoodData().setSaturation(Math.clamp(((int) currentSaturation)+1,0,20));
        }

        
    }
}

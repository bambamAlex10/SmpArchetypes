package com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidDomain;

import net.mc3699.provenance.ProvenanceDataHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent.LivingVisibilityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.VoidDomainAmbient;
import com.mc3699.smparch.archetype.fictionalbeef.ykorio.YkorioAttachments;
import com.mojang.logging.LogUtils;

@EventBusSubscriber(modid = SMPArch.MODID)
public class VoidDomainHandlers {

    public static final ResourceLocation SPEED_ID = ResourceLocation.fromNamespaceAndPath(SMPArch.MODID,
            "ykorio_speed_buff");
public static float getPlayerLightScaled(Player player) {
    BlockPos pos = player.blockPosition();
    Level level = player.level();
    
    int skyLight = level.getBrightness(net.minecraft.world.level.LightLayer.SKY, pos);
    int blockLight = level.getBrightness(net.minecraft.world.level.LightLayer.BLOCK, pos);
    
    float celestialAngle = level.getTimeOfDay(1.0f);
    float cosAngle = net.minecraft.util.Mth.cos(celestialAngle * ((float)Math.PI * 2.0F)) * 2.0F + 0.5F;
    cosAngle = net.minecraft.util.Mth.clamp(cosAngle, 0.0F, 1.0F);

    float sunBrightness = cosAngle * (1.0F - level.getRainLevel(1.0F)) * (1.0F - level.getThunderLevel(1.0F));

    int lightReduction = Math.round((1.0f - sunBrightness) * 15.0f);
    int currentSkyLight = Math.max(0, skyLight - lightReduction);
    int finalRawBrightness = Math.max(currentSkyLight, blockLight);

    return net.minecraft.util.Mth.clamp((7.5f-finalRawBrightness) / 7.5f, -1.0f, 1.0f);
}

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        LivingEntity beingAttacked = event.getEntity();

        if (!(beingAttacked instanceof Player attackedPlayer) || ProvenanceDataHandler
                .getAmbientAbilities(attackedPlayer).stream().noneMatch(ability -> ability instanceof VoidDomainAmbient)
                || attackedPlayer.level().isClientSide) {
            return;
        }

        float scaledPercentage = getPlayerLightScaled((ServerPlayer) attackedPlayer);

        float currentDamage = event.getNewDamage();
        float percentageDecrease = (.4f * scaledPercentage);
        currentDamage *= 1 - percentageDecrease;
        event.setNewDamage(currentDamage);

        // LogUtils.getLogger().debug("Percentage Decrease is "+percentageDecrease);
        // LogUtils.getLogger().debug("Scaled Percentage is "+scaledPercentage);
    }

    @SubscribeEvent
    public static void onLogin(PlayerLoggedInEvent event) {
        Player player = event.getEntity();

        if (ProvenanceDataHandler.getAmbientAbilities(player).stream().noneMatch(ability -> ability instanceof VoidDomainAmbient)) {
            return;
        }

        player.setData(YkorioAttachments.IMMUNITY,true);
        player.syncData(YkorioAttachments.IMMUNITY);
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player Player = event.getEntity();

        if (ProvenanceDataHandler.getAmbientAbilities(Player).stream()
                .noneMatch(ability -> ability instanceof VoidDomainAmbient) || Player.level().isClientSide) {
            return;
        }

        AttributeInstance speed = Player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null)
            return;

        speed.removeModifier(SPEED_ID);

        AttributeModifier currentSpeed = new AttributeModifier(SPEED_ID,
                .4f * getPlayerLightScaled((ServerPlayer) Player), AttributeModifier.Operation.ADD_MULTIPLIED_BASE);

        speed.addTransientModifier(currentSpeed);

    }

    @SubscribeEvent
    public static void onSetTarget(LivingVisibilityEvent event) {
        LivingEntity entity = event.getEntity();

        if (!(entity instanceof Player Player) || ProvenanceDataHandler.getAmbientAbilities(Player).stream()
                .noneMatch(ability -> ability instanceof VoidDomainAmbient) || Player.level().isClientSide) {
            return;
        }

        event.getVisibilityModifier();
        event.modifyVisibility(1-getPlayerLightScaled((ServerPlayer) Player));

    }
}
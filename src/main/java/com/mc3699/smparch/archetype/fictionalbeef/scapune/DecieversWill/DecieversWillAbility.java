package com.mc3699.smparch.archetype.fictionalbeef.scapune.DecieversWill;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import com.mc3699.smparch.SMPArch;
import com.mc3699.smparch.archetype.fictionalbeef.scapune.ScapuneAttachments;
import com.mc3699.smparch.archetype.fictionalbeef.scapune.ScapuneSounds;
import com.mc3699.smparch.registry.SMPSounds;
import com.mojang.logging.LogUtils;

import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class DecieversWillAbility extends BaseAbility {
    private static Random randomPitch = new Random();

    @Override
    public boolean canExecute(ServerPlayer arg0) {
        return true;
    }

    @Override
    public Component getName() {
        return Component.literal("Decievers Will");
    }

    @Override
    public float getUseCost() {
        return 1.0f;
    }

    @Override
    public int getCooldown() {
        return 30 * 60 * 20;
    }

    @Override
    public void execute(ServerPlayer player) {

        Level level = player.level();

        float Float2 = level.getRandom().nextFloat();
        boolean isDedicatedServer = net.neoforged.fml.loading.FMLEnvironment.dist.isDedicatedServer();

        if ((!isDedicatedServer && Float2 < .25f) || Float2 < .33f) { // his deception lies to even reality

            float levelofUltimate = 0.0f;
            int armorPoints = 0;

            for (net.minecraft.world.entity.EquipmentSlot slot : net.minecraft.world.entity.EquipmentSlot.values()) {

                if (slot.getType() != net.minecraft.world.entity.EquipmentSlot.Type.HUMANOID_ARMOR) {
                    continue;
                }
                ItemStack itemStack = player.getItemBySlot(slot);

                if (itemStack.isEmpty() || !itemStack.isDamageableItem()
                        || !(itemStack.getItem() instanceof ArmorItem armor)) {
                    continue;
                }

                armorPoints += armor.getDefense();
                itemStack.hurtAndBreak(100000, player.serverLevel(), player,
                        item -> player.onEquippedItemBroken(item, slot));

            }

            levelofUltimate = Math.clamp((armorPoints / 20f), 0.1f, 2.0f);

            int ticks = (int) (80f * levelofUltimate);

            player.setData(ScapuneAttachments.WILL_STRENGTH, levelofUltimate);
            player.setData(ScapuneAttachments.WILL_MOMENT, Instant.now());

            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, ticks * 20, 2));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, ticks * 20, 3));

            if (ticks >= 60) {
                player.level().playSound(null, player.getBlockPosBelowThatAffectsMyMovement(),
                        ScapuneSounds.DECIVRSLUCK.value(), SoundSource.PLAYERS, 0.35f,
                        1f);
            }

            return;
        }

        player.level().playSound(null, player.getBlockPosBelowThatAffectsMyMovement(),
                ScapuneSounds.DECIVRSWILL.value(), SoundSource.PLAYERS, 0.2f,
                randomPitch.nextFloat() * (1.2f - .8f) + 1f);

        final int POTION_COUNT = 10;
        final double SPREAD_ANGLE = 36.0;

        Vec3 lookVec = player.getLookAngle();
        Vec3 playerPos = player.getPosition(0);

        PotionContents Harming2 = new PotionContents(
                Optional.of(Potions.HARMING),
                Optional.of(0x6d0000),
                List.of(
                        new MobEffectInstance(MobEffects.HARM, 1, 2)));

        for (int projectileY = 0; projectileY < 3; projectileY++) {

            for (int i = 0; i < POTION_COUNT; i++) {
                double angleOffset = SPREAD_ANGLE * (i - (POTION_COUNT - 1) / 2.0);
                double radians = Math.toRadians(angleOffset);

                Vec3 spreadVec = new Vec3(
                        Math.cos(radians) * lookVec.x - Math.sin(radians) * lookVec.z,
                        lookVec.y,
                        Math.sin(radians) * lookVec.x + Math.cos(radians) * lookVec.z).normalize();

                double arcBoost = 1.5 + (projectileY * 0.5);
                Vec3 shootVec = new Vec3(
                        spreadVec.x,
                        spreadVec.y + arcBoost,
                        spreadVec.z).normalize();

                ThrownPotion potionEntity = new ThrownPotion(level, player);
                ItemStack entity = new ItemStack(Items.LINGERING_POTION);
                entity.set(DataComponents.POTION_CONTENTS, Harming2);

                potionEntity.setItem(entity);
                potionEntity.setPos(playerPos.x, playerPos.y + 1.5, playerPos.z);

                double speed = 0.4 + (projectileY * 0.2);
                potionEntity.shoot(shootVec.x, shootVec.y, shootVec.z, (float) speed, 0F);

                level.addFreshEntity(potionEntity);
            }


                Vec3 spreadVec = new Vec3(
                        0,
                        -3,
                        0).normalize();

                double arcBoost = 1.5 + (projectileY * 0.5);
                Vec3 shootVec = new Vec3(
                        spreadVec.x,
                        spreadVec.y + arcBoost,
                        spreadVec.z).normalize();

                ThrownPotion potionEntity = new ThrownPotion(level, player);
                ItemStack entity = new ItemStack(Items.LINGERING_POTION);
                entity.set(DataComponents.POTION_CONTENTS, Harming2);

                potionEntity.setItem(entity);
                potionEntity.setPos(playerPos.x, playerPos.y + 1.5, playerPos.z);

                double speed = 0.4 + (projectileY * 0.2);
                potionEntity.shoot(shootVec.x, shootVec.y, shootVec.z, (float) speed, 0F);

                level.addFreshEntity(potionEntity);

            level.playSound(null, player.blockPosition(), SoundEvents.SPLASH_POTION_THROW, SoundSource.PLAYERS, 0.8F,
                    1F);
        }
        player.kill();
    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.fromNamespaceAndPath(SMPArch.MODID, "textures/ability_icon/scapune/decieverswill.png");
    }

}

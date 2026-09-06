package com.mc3699.smparch.archetype.ariytwo6;

import com.ibm.icu.impl.CacheValue;
import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;

import java.util.Random;

public class StrengthrandAbility extends BaseAbility {
    @Override
    public float getUseCost() {
        return 2;
    }

    @Override
    public int getCooldown() {
        return 60*20 ;
    }

    @Override
    public Component getName() {
        return Component.literal("Roll For Strength");
    }

    @Override
    public boolean canExecute(ServerPlayer serverPlayer) {
        return true;
    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.withDefaultNamespace("textures/item/diamond_sword.png");
    }

    @Override
    public void execute(ServerPlayer player) {
        super.execute(player);
        int num = player.getRandom().nextInt(1,21);
        player.sendSystemMessage(Component.literal(String.valueOf(num)));
        if (num == 1) {
            player.die(player.damageSources().cramming());
        }
        if (num >=2 && num <=15) {
            player.sendSystemMessage(Component.literal("You Suck. Get better RNG"));
        }
        if (num >=16 && num <=19) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST , 15*20 , 0 , false , true));
        }
        if (num == 20) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST , 15*20 , 1 , false , true));
        }
    }
}

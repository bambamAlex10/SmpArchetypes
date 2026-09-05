package com.mc3699.smparch.archetype.tbs.miku;

import net.mc3699.provenance.ProvenanceDataHandler;
import net.mc3699.provenance.ability.foundation.BaseAbility;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.WeakHashMap;

public class WardenStrengthAbility extends BaseAbility {

    public static final Map<Player, Float> NEXT_ATTACK_BONUS =
            new WeakHashMap<>();

    @Override
    public float getUseCost() {
        return 1.5f;
    }

    @Override
    public int getCooldown() {
        return 10 * 40;
    }

    @Override
    public Component getName() {
        return Component.literal("Warden Strength");
    }

    @Override
    public boolean canExecute(ServerPlayer player) {
        return true;
    }

    @Override
    public void execute(ServerPlayer player) {
        super.execute(player);
        NEXT_ATTACK_BONUS.put(player, 10f);
    }

    @Override
    public void backgroundTick(ServerPlayer player) {
        super.backgroundTick(player);

        ResourceLocation id =
                ProvenanceDataHandler.getIdForAbility(player, this);
        if (id == null) return;

        int cooldown = ProvenanceDataHandler.getCooldown(player, id);

        if (cooldown <= 0) return;
        if (!NEXT_ATTACK_BONUS.containsKey(player)) return;

        if (cooldown > 1) {
            player.displayClientMessage(
                    Component.literal(
                            "Warden Strength is active! " + cooldown
                    ).withStyle(ChatFormatting.ITALIC, ChatFormatting.AQUA),
                    true
            );
        } else {
            NEXT_ATTACK_BONUS.remove(player);
            player.displayClientMessage(
                    Component.literal(
                            "Warden Strength has worn off. (Cooldown over)"
                    ).withStyle(ChatFormatting.ITALIC, ChatFormatting.AQUA),
                    true
            );
        }
    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.fromNamespaceAndPath(
                "minecraft",
                "textures/block/sculk_catalyst_top.png"
        );
    }
}

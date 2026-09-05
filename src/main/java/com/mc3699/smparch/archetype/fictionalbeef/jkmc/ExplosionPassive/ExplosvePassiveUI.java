package com.mc3699.smparch.archetype.fictionalbeef.jkmc.ExplosionPassive;

import com.mc3699.smparch.archetype.fictionalbeef.jkmc.JkmcAttachments;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;

public class ExplosvePassiveUI {
    public static final LayeredDraw.Layer HUD_LAYER = (graphics, deltaTracker) -> {
        if (Minecraft.getInstance().level == null || Minecraft.getInstance().player == null) {
            return;
        }

        LocalPlayer player = Minecraft.getInstance().player;
        if (player.hasData(JkmcAttachments.JKMC_CHARGE)) {
            float currentCharge = player.getData(JkmcAttachments.JKMC_CHARGE);
            float maxCharge = ExplosivePassiveHandlers.maxCharges * ExplosivePassiveHandlers.oneMeter;
            int percent = (int) ((currentCharge / maxCharge) * 100f);
            if (percent < 0)
                percent = 0;
            if (percent > 100)
                percent = 100;

            if (percent > 0) {
                int barX = (int) (graphics.guiWidth() * 0.015) + 3;
                int barY = (int) (graphics.guiHeight() * 0.01) + 5 + 70;
                renderChargeBar(graphics, barX, barY, 64, 8, percent, Math.round((currentCharge/ExplosivePassiveHandlers.oneMeter*100.0f))/100.0f);
            }
        }
    };

    private static void renderChargeBar(GuiGraphics graphics, int x, int y, int width, int height, int percent,
            float currentCharge) {
        // Background (dark grey)
        graphics.fill(x, y, x + width, y + height, 0xFF333333);

        int color;
        if (percent < 30) {
            color = 0xFFFF0000; // red
        } else if (percent < 70) {
            color = 0xFFFFAA00; // orange/yellow
        } else {
            color = 0xFF00CC00; // green
        }

        int filledWidth = (int) (width * (percent / 100f));
        if (filledWidth > 0) {
            // Inset by 1 pixel for a small border
            graphics.fill(x + 1, y + 1, x + filledWidth - 1, y + height - 1, color);
        }

        // Optional: draw a white outline
        graphics.renderOutline(x, y, width, height, 0xFFFFFFFF);

        // Centered text on top
        Font font = Minecraft.getInstance().font;
        String label = String.valueOf(currentCharge); // + " charges"; // or percent + "%"
        int textX = x + width / 2;
        int textY = y + (height - font.lineHeight) / 2 + 1; // vertical centering
        graphics.drawCenteredString(font, label, textX, textY, 0xFFFFFF);
    }
}

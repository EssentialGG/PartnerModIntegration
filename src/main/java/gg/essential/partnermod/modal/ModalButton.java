/*
 * Copyright © 2025 ModCore Inc. All rights reserved.
 *
 * This code is part of ModCore Inc.’s Essential Partner Mod Integration
 * repository and is protected under copyright. For the full license, see:
 * https://github.com/EssentialGG/EssentialPartnerMod/tree/main/LICENSE
 *
 * You may modify, fork, and use the Mod, but may not retain ownership of
 * accepted contributions, claim joint ownership, or use Essential’s trademarks.
 */

package gg.essential.partnermod.modal;

import gg.essential.partnermod.Draw;
import gg.essential.partnermod.Tooltip;
import net.minecraft.client.Minecraft;

public class ModalButton {

    public final int x;
    public final int y;
    public final int width;
    public final int height;
    public final ButtonColor color;
    public final String text;
    private final Runnable onClick;
    private String tooltip;

    public ModalButton(int x, int y, int width, int height, ButtonColor color, String text, Runnable onClick) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
        this.text = text;
        this.onClick = onClick;
    }

    public ModalButton(int x, int y, int width, ButtonColor color, String text, Runnable onClick) {
        this(x, y, width, 19, color, text, onClick);
    }

    public void draw(Draw draw) {
        boolean hovered = draw.hovered(x, y, width, height);

        // shadow
        draw.rect(x + 1, y + 1, x + 1 + width, y + 1 + height, color.getShadowColor(hovered));
        // outline
        draw.rect(x, y, x + width, y + height, color.getHighlightColor(hovered));
        // main background
        draw.rect(x + 1, y + 1, x + width - 1, y + height - 1, color.getButtonColor(hovered));

        if (!text.isEmpty()) {
            int textX = x + width / 2 - Minecraft.getMinecraft().fontRenderer.getStringWidth(text) / 2;
            draw.string(text, textX, y + 6, 0xFFE5E5E5, 0xFF000000);
        }

        if (tooltip != null && !tooltip.isEmpty() && hovered) {
            Draw.deferred(d -> Tooltip.drawTooltip(d, tooltip, Tooltip.Position.ABOVE, x, y, width, height));
        }
    }

    public void onClick() {
        this.onClick.run();
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }
}

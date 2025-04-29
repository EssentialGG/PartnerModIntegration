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
import gg.essential.partnermod.Resources;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class LinkButton extends ModalButton {

    private static final ResourceLocation ICON = Resources.load("link.png");

    public LinkButton(int x, int y, String text, Runnable onClick) {
        super(x, y, Minecraft.getMinecraft().fontRenderer.getStringWidth(text) + 10, 11, null, text, onClick);
    }

    @Override
    public void draw(Draw draw) {
        int color = draw.hovered(x, y, width, height) ? 0xFF757575 : 0xFF5C5C5C;
        draw.string(text, x, y, color, 0xFF000000);

        int textWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(text);

        draw.texturedRect(ICON, x + textWidth + 5, y + 2, 5, 5, 0, 0, 5, 5, 0xFF000000);
        draw.texturedRect(ICON, x + textWidth + 4, y + 1, 5, 5, 0, 0, 5, 5, color);

        draw.rect(x, y + 8, x + textWidth, y + 9, color);
        draw.rect(x + 1, y + 9, x + textWidth + 1, y + 10, 0xFF000000);
    }
}

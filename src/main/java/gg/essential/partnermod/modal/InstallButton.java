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

public class InstallButton extends ModalButton {
    private static final ResourceLocation ICON = Resources.load("download.png");

    public InstallButton(int x, int y, Runnable onClick) {
        super(x, y, 140, ButtonColor.BLUE, "", onClick);
    }

    @Override
    public void draw(Draw draw) {
        super.draw(draw);

        String text = "Install Now";
        int textWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(text) + 12;

        int centreX = x + width / 2;
        int startX = centreX - textWidth / 2;
        draw.texturedRect(ICON, startX + 1, y + 6, 10, 10, 0, 0, 10, 10, 0xFF132339);
        draw.texturedRect(ICON, startX, y + 5, 10,  10, 0, 0, 10, 10, 0xFFe5e5e5);
        draw.string(text, startX + 12, y + 6, 0xFFe5e5e5, 0xFF132339);
    }
}

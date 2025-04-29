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
import net.minecraft.util.ResourceLocation;

public class TextureButton extends ModalButton {

    private final ResourceLocation texture;
    private final int color;
    private final int hoverColor;
    private final boolean shadow;

    public TextureButton(int x, int y, int width, int height, ResourceLocation texture, int color, int hoverColor, boolean shadow, Runnable onClick) {
        super(x, y, width, height, null, "", onClick);
        this.color = color;
        this.hoverColor = hoverColor;
        this.texture = texture;
        this.shadow = shadow;
    }

    @Override
    public void draw(Draw draw) {
        int drawColor = draw.hovered(x, y, width, height) ? hoverColor : color;
        if (shadow) {
            draw.texturedRect(texture, x + 1, y + 1, width, height,  0, 0, width, height, 0xFF000000);
        }
        draw.texturedRect(texture, x, y, width, height, 0, 0, width, height, drawColor);
    }
}

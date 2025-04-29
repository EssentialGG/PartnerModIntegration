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

package gg.essential.partnermod;

import gg.essential.partnermod.mc.Font;

public class Tooltip {

    public static void drawTooltip(Draw draw, String text, Position position, int parentX, int parentY, int parentWidth, int parentHeight) {
        String[] lines = text.split("\n");
        int maxWidth = 0;
        for (String line : lines) {
            maxWidth = Math.max(maxWidth, Font.getStringWidth(line));
        }

        int width = maxWidth + 8;
        int height = 9 * lines.length + 6;

        int x;
        int y;
        switch (position) {
            case ABOVE:
                x = parentX + parentWidth / 2 - width / 2;
                y = parentY - height - 5;
                break;
            case BELOW:
                x = parentX + parentWidth / 2 - width / 2;
                y = parentY + parentHeight + 5;
                break;
            case LEFT:
                x = parentX - width - 5;
                y = parentY + parentHeight / 2 - height / 2;
                break;
            case RIGHT:
                x = parentX + parentWidth + 5;
                y = parentY + parentHeight / 2 - height / 2;
                break;
            default:
                throw new IllegalStateException();
        }

        int centerX = x + width / 2;
        int centerY = y + height / 2;

        draw.rect(x - 1, y - 1, x + width + 1, y + height + 1, 0xFF000000);
        draw.rect(x, y, x + width, y + height, 0xFF232323);

        int textY = y;
        for (String line : lines) {
            int lineWidth = Font.getStringWidth(line);
            draw.string(line, centerX - lineWidth / 2, textY + 3, -1);
            textY += 10;
        }

        for (int i = 0; i <= 2; i++) {
            switch (position) {
                case ABOVE:
                    draw.rect(centerX - (2 - i) - 1, y + height + i + 1, centerX + (2 - i), y + height + i + 2, 0xFF000000);
                    draw.rect(centerX - (2 - i) - 1, y + height + i, centerX + (2 - i), y + height + i + 1, 0xFF232323);
                    break;
                case BELOW:
                    draw.rect(centerX - (2 - i) - 1, y - i - 2, centerX + (2 - i), y - i - 1, 0xFF000000);
                    draw.rect(centerX - (2 - i) - 1, y - i - 1, centerX + (2 - i), y - i, 0xFF232323);
                    break;
                case LEFT:
                    draw.rect(x + width + i + 1, centerY - (2 - i) - 1, x + width + i + 2, centerY + (2 - i), 0xFF000000);
                    draw.rect(x + width + i, centerY - (2 - i) - 1, x + width + i + 1, centerY + (2 - i), 0xFF232323);
                    break;
                case RIGHT:
                    draw.rect(x - i - 2, centerY - (2 - i) - 1, x - i - 1, centerY + (2 - i), 0xFF000000);
                    draw.rect(x - i - 1, centerY - (2 - i) - 1, x - i, centerY + (2 - i), 0xFF232323);
                    break;
            }
        }
    }

    public enum Position {
        ABOVE,
        BELOW,
        LEFT,
        RIGHT,
    }

}

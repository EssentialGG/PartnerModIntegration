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
import gg.essential.partnermod.UResolution;
import net.minecraft.client.gui.Gui;

import java.util.ArrayList;
import java.util.List;

//#if MC>=11600
//$$ import org.lwjgl.glfw.GLFW;
//#else
import org.lwjgl.input.Keyboard;
//#endif

public abstract class Modal
    //#if MC<12000
    extends Gui
    //#endif
{

    protected int width;
    protected int height;
    protected int centreX;
    protected int centreY;
    protected int startX;
    protected int startY;

    protected void setDimensions(int width, int height) {
        this.width = width;
        this.height = height;
        this.centreX = UResolution.getScaledWidth() / 2;
        this.centreY = UResolution.getScaledHeight() / 2;
        this.startX = centreX - width / 2;
        this.startY = centreY - height / 2;
    }

    protected List<ModalButton> buttonList = new ArrayList<>();

    public void init() {
        buttonList.clear();
    }

    public void draw(Draw draw) {
        draw.rect(0, 0, UResolution.getScaledWidth(), UResolution.getScaledHeight(), 0x96000000);

        drawBackground(draw);

        for (ModalButton button : buttonList) {
            button.draw(draw);
        }
    }

    protected void drawBackground(Draw draw) {
        // border
        draw.rect(startX, startY, startX + width, startY + height, 0xFF474747);
        // background
        draw.rect(startX + 1, startY + 1, startX + width - 1, startY + height - 1, 0xFF181818);
    }

    public void mouseClicked(int mouseX, int mouseY) {
        for (ModalButton button : buttonList) {
            if (Draw.hovered(mouseX, mouseY, button.x, button.y, button.width, button.height)) {
                button.onClick();
                return;
            }
        }
        // Anywhere on the modal
        if (Draw.hovered(mouseX, mouseY, startX, startY, width, height)) {
            return;
        }
        // Clicked on the modal background, close the modal
        ModalManager.INSTANCE.setModal(null);
    }

    public void keyPressed(int key) {
        //#if MC>=11600
        //$$ if (key == GLFW.GLFW_KEY_ESCAPE) {
        //#else
        if (key == Keyboard.KEY_ESCAPE) {
        //#endif
            ModalManager.INSTANCE.setModal(null);
        }
    }

    public void close() {

    }

}

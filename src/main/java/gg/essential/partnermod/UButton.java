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

import net.minecraft.client.gui.GuiButton;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

//#if MC>=11600
//$$ import net.minecraft.client.gui.widget.Widget;
//#endif

//#if MC>=11900
//$$ import net.minecraft.text.Text;
//#elseif MC>=11600
//$$ import net.minecraft.util.text.TranslationTextComponent;
//#else
import net.minecraft.client.resources.I18n;
//#endif

public class UButton {

    public static int getWidth(GuiButton button) {
        //#if MC>=11600
        //$$ return button.getWidth();
        //#else
        return button.width;
        //#endif
    }

    public static int getHeight(GuiButton button) {
        //#if MC>=11600
        //$$ return button.getHeight();
        //#else
        return button.height;
        //#endif
    }

    public static int getX(GuiButton button) {
        //#if MC>=11903
        //$$ return button.getX();
        //#else
        return button.x;
        //#endif
    }

    public static int getY(GuiButton button) {
        //#if MC>=11903
        //$$ return button.getY();
        //#else
        return button.y;
        //#endif
    }

    public static GuiButton findButton(
        //#if MC>=11600
        //$$ List<Widget> buttonList,
        //#else
        List<GuiButton> buttonList,
        //#endif
        Set<String> translationKeys
    ) {
        //#if MC>=11600
        //#if MC>=11900
        //$$ Set<Text> texts = translationKeys.stream().map(Text::translatable).collect(Collectors.toSet());
        //#else
        //$$ Set<TranslationTextComponent> texts = translationKeys.stream().map(TranslationTextComponent::new).collect(Collectors.toSet());
        //#endif
        //$$ for (Widget widget : buttonList) {
        //$$     if (widget instanceof Button && texts.contains(widget.getMessage())) {
        //$$         return (Button) widget;
        //$$     }
        //$$ }
        //#else
        Set<String> texts = translationKeys.stream().map(I18n::format).collect(Collectors.toSet());
        for (GuiButton guiButton : buttonList) {
            if (texts.contains(guiButton.displayString)) {
                return guiButton;
            }
        }
        //#endif
        return null;
    }
}

package gg.essential.partnermod.mc;

import net.minecraft.client.Minecraft;

public class Font {

    public static int getStringWidth(String text) {
        return Minecraft.getMinecraft().fontRenderer.getStringWidth(text);
    }

    public static int getMultilineStringHeight(String text, int lineSpacing) {
        int lines = text.split("\n").length;
        int space = lineSpacing - 10;
        return 10 * lines + space * (lines - 1);
    }

}

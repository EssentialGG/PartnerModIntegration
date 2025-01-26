package gg.essential.ad.modal;

import gg.essential.ad.Draw;
import gg.essential.ad.mc.UMinecraft;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class LinkButton extends ModalButton {

    private static final ResourceLocation ICON = UMinecraft.identifier("essentialad", "link.png");

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

        draw.rect(x, y + 8, x + width, y + 9, color);
        draw.rect(x + 1, y + 9, x + width + 1, y + 10, 0xFF000000);
    }
}

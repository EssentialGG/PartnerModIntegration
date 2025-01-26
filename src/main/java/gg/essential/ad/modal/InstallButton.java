package gg.essential.ad.modal;

import gg.essential.ad.Draw;
import gg.essential.ad.mc.UMinecraft;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class InstallButton extends ModalButton {
    private static final ResourceLocation ICON = UMinecraft.identifier("essentialad", "download.png");

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

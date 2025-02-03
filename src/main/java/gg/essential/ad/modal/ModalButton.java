package gg.essential.ad.modal;

import gg.essential.ad.Draw;
import gg.essential.ad.Tooltip;
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
        this(x, y, width, 20, color, text, onClick);
    }

    public void draw(Draw draw) {
        boolean hovered = draw.hovered(x, y, width, height);

        // outline
        draw.rect(x, y, x + width, y + height, hovered ? 0xFFe5e5e5 : 0xFF000000);
        // main background
        draw.rect(x + 1, y + 1, x + width - 1, y + height - 1, color.getButtonColor(hovered));
        // highlight
        draw.rect(x + 1, y + 1, x + 2, y + height - 3, color.getHighlightColor(hovered));
        draw.rect(x + 2, y + 1, x + width - 2, y + 2, color.getHighlightColor(hovered));
        // shadow
        draw.rect(x + 2, y + height - 3, x + width - 1, y + height - 1, color.getShadowColor(hovered));
        draw.rect(x + width - 2, y + 2, x + width - 1, y + height - 3, color.getShadowColor(hovered));

        if (!text.isEmpty()) {
            int textX = x + width / 2 - Minecraft.getMinecraft().fontRenderer.getStringWidth(text) / 2;
            draw.string(text, textX, y + 6, 0xFFE5E5E5, 0x181818);
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

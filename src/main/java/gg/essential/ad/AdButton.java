package gg.essential.ad;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.util.function.Consumer;

//#if MC>=11600
//$$ import com.mojang.blaze3d.matrix.MatrixStack;
//#endif

//#if MC>=11700
//$$ import net.minecraft.client.render.GameRenderer;
//#endif

//#if MC>=11900
//$$ import net.minecraft.text.Text;
//#elseif MC>=11600
//$$ import net.minecraft.util.text.StringTextComponent;
//#endif

//#if MC>=12000
//$$ import net.minecraft.client.gui.DrawContext;
//#endif

public class AdButton extends GuiButton {

    private static final int BUTTON_ID = 0xe4c164f1;

    private static final ResourceLocation TEXTURE =
        //#if MC>=12100
        //$$ Identifier.of("essentialad", "button.png");
        //#else
        new ResourceLocation("essentialad", "button.png");
        //#endif

    private final int texYOffset;
    private final String tooltip;
    //#if MC<11600
    private final Consumer<GuiButton> onPress;
    //#endif

    public AdButton(int x, int y, int texYOffset, Consumer<GuiButton> onPress, String tooltip) {
        //#if MC>=11903
        //$$ super(x, y, 20, 20, Text.empty(), onPress::accept, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
        //#elseif MC>=11900
        //$$ super(x, y, 20, 20, Text.empty(), onPress::accept);
        //#elseif MC>=11600
        //$$ super(x, y, 20, 20, StringTextComponent.EMPTY, onPress::accept);
        //#else
        super(BUTTON_ID, x, y, 20, 20, "");
        //#endif
        this.texYOffset = texYOffset;
        this.tooltip = tooltip;
        //#if MC<11600
        this.onPress = onPress;
        //#endif
    }

    //#if MC>=11600
    //$$ @Override
    //#if MC>=12000
    //$$ public void renderButton(DrawContext context, int mouseX, int mouseY, float partialTicks) {
    //#else
    //$$ public void renderWidget(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    //#endif
    //$$     if (visible) {
            //#if MC>=12000
            //#elseif MC>=11700
            //$$ RenderSystem.setShader(GameRenderer::getPositionTexShader);
            //$$ RenderSystem.setShaderTexture(0, TEXTURE);
            //#else
            //$$ Minecraft.getInstance().getTextureManager().bindTexture(TEXTURE);
            //#endif
    //$$         int x = 0;
    //$$         if (this.isHovered()) x += this.width;
    //$$
            //#if MC>=12000
            //$$ context.drawTexture(TEXTURE, this.getX(), this.getY(), 0, x, texYOffset, width, height, 256, 256);
            //#elseif MC>=11903
            //$$ drawTexture(matrixStack, this.getX(), this.getY(), 0, x, texYOffset, width, height, 256, 256);
            //#else
            //$$ blit(matrixStack, this.x, this.y, 0, x, texYOffset, width, height, 256, 256);
            //#endif
    //$$         if (this.isHovered()) {
                //#if MC>=12000
                //$$ drawTooltip(new Draw(mouseX, mouseY, context), MinecraftClient.getInstance(), TooltipPosition.ABOVE);
                //#else
                //$$ drawTooltip(new Draw(mouseX, mouseY, matrixStack), Minecraft.getInstance(), TooltipPosition.ABOVE);
                //#endif
    //$$         }
    //$$     }
    //$$ }
    //#else
    @Override
    public void drawButton(
        Minecraft mc, int mouseX, int mouseY
        //#if MC>=11200
        , float partialTicks
        //#endif
    ) {
        if (this.visible) {
            mc.getTextureManager().bindTexture(TEXTURE);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            boolean hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int x = 0;
            if (hovered) x += this.width;

            this.drawTexturedModalRect(this.x, this.y, x, texYOffset, this.width, this.height);

            if (hovered) {
                //fixme depth
                drawTooltip(new Draw(mouseX, mouseY), mc, TooltipPosition.ABOVE);
            }
        }
    }
    //#endif

    private void drawTooltip(Draw draw, Minecraft mc, TooltipPosition position) {
        String[] lines = this.tooltip.split("\n");
        int maxWidth = 0;
        for (String line : lines) {
            maxWidth = Math.max(maxWidth, mc.fontRenderer.getStringWidth(line));
        }

        int width = maxWidth + 8;
        int height = 9 * lines.length + 6;

        int buttonX = UButton.getX(this);
        int buttonY = UButton.getY(this);

        int x;
        int y;
        switch (position) {
            case ABOVE:
                x = buttonX + this.width / 2 - width / 2;
                y = buttonY - height - 5;
                break;
            case BELOW:
                x = buttonX + this.width / 2 - width / 2;
                y = buttonY + this.height + 5;
                break;
            case LEFT:
                x = buttonX - width - 5;
                y = buttonY + this.height / 2 - height / 2;
                break;
            case RIGHT:
                x = buttonX + this.width + 5;
                y = buttonY + this.height / 2 - height / 2;
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
            int lineWidth = mc.fontRenderer.getStringWidth(line);
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
                    draw.rect(centerX - (2 - i) - 1, y - i - 1, centerX + (2 - i), y - i - 2, 0xFF000000);
                    draw.rect(centerX - (2 - i) - 1, y - i, centerX + (2 - i), y - i - 1, 0xFF232323);
                    break;
                case LEFT:
                    draw.rect(x + width + i + 1, centerY - (2 - i) - 1, x + width + i + 2, centerY + (2 - i), 0xFF000000);
                    draw.rect(x + width + i, centerY - (2 - i) - 1, x + width + i + 1, centerY + (2 - i), 0xFF232323);
                    break;
                case RIGHT:
                    draw.rect(x - i - 1, centerY - (2 - i) - 1, x - i - 2, centerY + (2 - i), 0xFF000000);
                    draw.rect(x - i, centerY - (2 - i) - 1, x - i - 1, centerY + (2 - i), 0xFF232323);
                    break;
            }
        }
    }

    enum TooltipPosition {
        ABOVE,
        BELOW,
        LEFT,
        RIGHT,
    }

    //#if MC<11600
    public void onPress() {
        onPress.accept(this);
    }
    //#endif
}

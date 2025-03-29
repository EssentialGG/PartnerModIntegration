package gg.essential.partnermod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

//#if MC>12102
//$$ import net.minecraft.client.render.RenderLayer;
//#endif

//#if MC>=11600
//$$ import com.mojang.blaze3d.matrix.MatrixStack;
//#endif

public class Draw {

    public final int mouseX;
    public final int mouseY;

    //#if MC>=12000
    //$$ public final DrawContext drawContext;
    //#elseif MC>=11600
    //$$ public final MatrixStack matrixStack;
    //#endif

    private static final List<Consumer<Draw>> deferred = new ArrayList<>();

    public Draw(
            int mouseX,
            int mouseY
            //#if MC>=12000
            //$$ , DrawContext drawContext
            //#elseif MC>=11600
            //$$ , MatrixStack matrixStack
            //#endif
    ) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        //#if MC>=12000
        //$$ this.drawContext = drawContext;
        //#elseif MC>=11600
        //$$ this.matrixStack = matrixStack;
        //#endif
    }

    public void rect(int left, int top, int right, int bottom, int color) {
        //#if MC>=12000
        //$$ drawContext.fill(left, top, right, bottom, color);
        //#elseif MC>=11600
        //$$ AbstractGui.fill(matrixStack, left, top, right, bottom, color);
        //#else
        Gui.drawRect(left, top, right, bottom, color);
        //#endif
    }

    public void texturedRect(ResourceLocation texture, int x, int y, int width, int height, int u, int v, int textureWidth, int textureHeight) {
        texturedRect(texture, x, y, width, height, u, v, textureWidth, textureHeight, 0xFFFFFFFF);
    }

    public void texturedRect(ResourceLocation texture, int x, int y, int width, int height, int u, int v, int textureWidth, int textureHeight, int color) {
        //#if MC>=12102
        //$$ drawContext.drawTexture(RenderLayer::getGuiTextured, texture, x, y, u, v, width, height, textureWidth, textureHeight, color);
        //#else
        float red = ((color >> 16) & 0xFF) / 255f;
        float green = ((color >> 8) & 0xFF) / 255f;
        float blue = (color & 0xFF) / 255f;
        //#if MC>=11700
        //$$ RenderSystem.setShaderColor(red, green, blue, 1f);
        //#if MC<12000
        //$$ RenderSystem.setShaderTexture(0, texture);
        //#endif
        //#else
        GlStateManager.color(red, green, blue, 1f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        //#endif
        //#if MC>=12000
        //$$ drawContext.drawTexture(texture, x, y, u, v, width, height, textureWidth, textureHeight);
        //#elseif MC>=11600
        //$$ AbstractGui.blit(matrixStack, x, y, u, v, width, height, textureWidth, textureHeight);
        //#else
        Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, width, height, textureWidth, textureHeight);
        //#endif
        //#if MC>=11700
        //$$ RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        //#endif
        //#endif
    }

    public void string(String text, int x, int y, int color) {
        //#if MC>=12000
        //$$ drawContext.drawText(MinecraftClient.getInstance().textRenderer, text, x, y, color, false);
        //#elseif MC>=11600
        //$$ Minecraft.getInstance().fontRenderer.drawString(matrixStack, text, x, y, color);
        //#else
        Minecraft.getMinecraft().fontRenderer.drawString(text, x, y, color);
        //#endif
    }

    public void string(String text, int x, int y, int color, int shadowColor) {
        string(text, x + 1, y + 1, shadowColor);
        string(text, x, y, color);
    }

    public void centredString(String text, int centreX, int y, int color, int shadowColor) {
        int width = Minecraft.getMinecraft().fontRenderer.getStringWidth(text);
        string(text, centreX - width / 2, y, color, shadowColor);
    }

    public void multilineCentredString(String text, int centreX, int y, int lineSpacing, int color, int shadowColor) {
        for (String line : text.split("\n")) {
            centredString(line, centreX, y, color, shadowColor);
            y += lineSpacing;
        }
    }

    public static boolean hovered(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    public boolean hovered(int x, int y, int width, int height) {
        return hovered(mouseX, mouseY, x, y, width, height);
    }

    public static void deferred(Consumer<Draw> drawer) {
        deferred.add(drawer);
    }

    public static void flushDeferred(Draw draw) {
        for (Consumer<Draw> drawConsumer : deferred) {
            drawConsumer.accept(draw);
        }
        deferred.clear();
    }

}

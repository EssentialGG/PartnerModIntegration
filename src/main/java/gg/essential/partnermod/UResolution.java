package gg.essential.partnermod;

import net.minecraft.client.Minecraft;

//#if MC<11600
import net.minecraft.client.gui.ScaledResolution;
//#endif

public class UResolution {

    public static int getWindowWidth() {
        //#if MC>=11600
        //$$ return Minecraft.getInstance().getMainWindow().getWidth();
        //#else
        return Minecraft.getMinecraft().displayWidth;
        //#endif
    }

    public static int getWindowHeight() {
        //#if MC>=11600
        //$$ return Minecraft.getInstance().getMainWindow().getHeight();
        //#else
        return Minecraft.getMinecraft().displayHeight;
        //#endif
    }

    public static int getScaledWidth() {
        //#if MC>11600
        //$$ return Minecraft.getInstance().getMainWindow().getScaledWidth();
        //#else
        return new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth();
        //#endif
    }

    public static int getScaledHeight() {
        //#if MC>11600
        //$$ return Minecraft.getInstance().getMainWindow().getScaledHeight();
        //#else
        return new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight();
        //#endif
    }

}

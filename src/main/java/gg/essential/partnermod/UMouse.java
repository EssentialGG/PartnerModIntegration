package gg.essential.partnermod;

import net.minecraft.client.Minecraft;

//#if MC<11600
import org.lwjgl.input.Mouse;
//#endif

public class UMouse {

    public static double getRawX() {
        //#if MC>=11600
        //$$ return Minecraft.getInstance().mouseHelper.getMouseX();
        //#else
        return Mouse.getX();
        //#endif
    }

    public static double getRawY() {
        //#if MC>=11600
        //$$ return Minecraft.getInstance().mouseHelper.getMouseY();
        //#else
        return Minecraft.getMinecraft().displayHeight - Mouse.getY() - 1;
        //#endif
    }

    public static double getScaledX() {
        return getRawX() * UResolution.getScaledWidth() / Math.max(1, UResolution.getWindowWidth());
    }

    public static double getScaledY() {
        return getRawY() * UResolution.getScaledHeight() / Math.max(1, UResolution.getWindowHeight());
    }

}

package gg.essential.ad.modal;

import gg.essential.ad.Draw;
import gg.essential.ad.UMouse;
import gg.essential.ad.UResolution;
import net.minecraft.client.Minecraft;

//#if FABRIC
//$$ import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
//$$ import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
//$$ import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
//#else
//#if MC>=11800
//$$ import net.minecraftforge.client.event.ScreenEvent;
//#else
import net.minecraftforge.client.event.GuiScreenEvent;
//#endif
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
//#endif

//#if MC>=12000
//$$ import net.minecraft.client.gui.DrawContext;
//#endif

//#if MC>=11600
//$$ import com.mojang.blaze3d.matrix.MatrixStack;
//$$ import gg.essential.ad.mixins.MouseHelperAccessor;
//#else
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
//#endif

public class ModalManager {

    public static final ModalManager INSTANCE = new ModalManager();
    private static final double FAKE_MOUSE_POS = -1e6;

    private Modal currentModal = null;
    private MousePosition mousePosition = null;

    private int previousWidth = -1;
    private int previousHeight = -1;

    public void setModal(Modal modal) {
        if (currentModal != null) {
            currentModal.close();
        }
        previousWidth = -1;
        previousHeight = -1;
        this.currentModal = modal;
    }

    public MousePosition getMousePosition() {
        return mousePosition;
    }

    private void setMousePosisiton(double x, double y) {
        mousePosition = new MousePosition(x, y, UMouse.getRawX(), UMouse.getRawY());
        GlobalMouseOverride.set(x, y);
    }

    private void resetMousePosition() {
        GlobalMouseOverride.set(mousePosition.originalMouseX, mousePosition.originalMouseY);
        mousePosition = null;
    }

    private void handlePreDraw(DrawEvent event) {
        if (currentModal == null) return;

        setMousePosisiton(FAKE_MOUSE_POS, FAKE_MOUSE_POS);
        event.mouseX = (int) FAKE_MOUSE_POS;
        event.mouseY = (int) FAKE_MOUSE_POS;
    }

    private void handleDraw(
        //#if MC>=12000
        //$$ DrawContext stack
        //#elseif MC>=11600
        //$$ MatrixStack stack
        //#endif
    ) {
        if (currentModal == null) return;

        resetMousePosition();

        int width = UResolution.getScaledWidth();
        int height = UResolution.getScaledHeight();

        if (this.previousWidth != width || this.previousHeight != height) {
            currentModal.init();
        }

        Draw draw = new Draw(
            (int) UMouse.getScaledX(),
            (int) UMouse.getScaledY()
            //#if MC>=11600
            //$$ , stack
            //#endif
        );

        currentModal.draw(draw);
        draw.flushDeferred();
    }

    private boolean handleMouseClick(double mouseX, double mouseY) {
        if (currentModal == null) return false;
        currentModal.mouseClicked((int) mouseX, (int) mouseY);
        return true;
    }

    private boolean handleKeyTyped(int scanCode) {
        if (currentModal == null) return false;
        currentModal.keyPressed(scanCode);
        return true;
    }

    //// Events \\\\

    //#if FABRIC
    //$$ public void registerEvents() {
    //$$     ScreenEvents.BEFORE_INIT.register(((client, screen, scaledWidth, scaledHeight) -> {
    //$$         ScreenEvents.beforeRender(screen).register((screen1, matrices, mouseX, mouseY, tickDelta) -> {
    //$$             handlePreDraw(new DrawEvent(mouseX, mouseY));
    //$$         });
    //$$         ScreenEvents.afterRender(screen).register((screen1, matrices, mouseX, mouseY, tickDelta) -> {
    //$$             handleDraw(matrices);
    //$$         });
    //$$         ScreenMouseEvents.allowMouseClick(screen).register((screen1, mouseX, mouseY, button) -> {
    //$$             return !handleMouseClick(mouseX, mouseY);
    //$$         });
    //$$         ScreenMouseEvents.allowMouseScroll(screen).register((screen1, mouseX, mouseY, horizontalAmount, verticalAmount) -> {
    //$$             return !handleMouseClick(mouseX, mouseY);
    //$$         });
    //$$         ScreenKeyboardEvents.allowKeyPress(screen).register((screen1, key, scancode, modifiers) -> {
    //$$             return !handleKeyTyped(key);
    //$$         });
    //$$     }));
    //$$ }
    //#else
    //#if MC>=11900
    //$$ @SubscribeEvent
    //$$ public void preDraw(ScreenEvent.Render.Pre event) {
    //$$     handlePreDraw(new DrawEvent(event.getMouseX(), event.getMouseY()));
    //$$ }
    //$$
    //$$ @SubscribeEvent
    //$$ public void draw(ScreenEvent.Render.Post event) {
        //#if MC>=12000
        //$$ handleDraw(event.getGuiGraphics());
        //#else
        //$$ handleDraw(event.getPoseStack());
        //#endif
    //$$ }
    //$$
    //$$ @SubscribeEvent
    //$$ public void mouseClicked(ScreenEvent.MouseButtonPressed.Pre event) {
    //$$     event.setCanceled(handleMouseClick(event.getMouseX(), event.getMouseY()));
    //$$ }
    //$$
    //$$ @SubscribeEvent
    //$$ public void keyPressed(ScreenEvent.KeyPressed.Pre event) {
    //$$     event.setCanceled(handleKeyTyped(event.getScanCode()));
    //$$ }
    //#elseif MC>=11800
    //$$ @SubscribeEvent
    //$$ public void preDraw(ScreenEvent.DrawScreenEvent.Pre event) {
    //$$     handlePreDraw(new DrawEvent(event.getMouseX(), event.getMouseY()));
    //$$ }
    //$$
    //$$ @SubscribeEvent
    //$$ public void draw(ScreenEvent.DrawScreenEvent.Post event) {
    //$$     handleDraw(event.getPoseStack());
    //$$ }
    //$$
    //$$ @SubscribeEvent
    //$$ public void mouseClicked(ScreenEvent.MouseClickedEvent.Pre event) {
    //$$     event.setCanceled(handleMouseClick(event.getMouseX(), event.getMouseY()));
    //$$ }
    //$$
    //$$ @SubscribeEvent
    //$$ public void keyPressed(ScreenEvent.KeyboardKeyPressedEvent.Pre event) {
    //$$     event.setCanceled(handleKeyTyped(event.getScanCode()));
    //$$ }
    //#elseif MC>=11600
    //$$ @SubscribeEvent
    //$$ public void preDraw(GuiScreenEvent.DrawScreenEvent.Pre event) {
    //$$     handlePreDraw(new DrawEvent(event.getMouseX(), event.getMouseY()));
    //$$ }
    //$$
    //$$ @SubscribeEvent
    //$$ public void draw(GuiScreenEvent.DrawScreenEvent.Post event) {
    //$$     handleDraw(event.getMatrixStack());
    //$$ }
    //$$
    //$$ @SubscribeEvent
    //$$ public void mouseClicked(GuiScreenEvent.MouseClickedEvent.Pre event) {
    //$$     event.setCanceled(handleMouseClick(event.getMouseX(), event.getMouseY()));
    //$$ }
    //$$
    //$$ @SubscribeEvent
    //$$ public void keyPressed(GuiScreenEvent.KeyboardKeyPressedEvent.Pre event) {
    //$$     event.setCanceled(handleKeyTyped(event.getScanCode()));
    //$$ }
    //#else
    public static void preDraw(DrawEvent event) {
        INSTANCE.handlePreDraw(event);
    }

    public static void drawScreenPriority(
        //#if MC>=11600
        //$$ MatrixStack stack
        //#endif
    ) {
        INSTANCE.handleDraw(
            //#if MC>=11600
            //$$ stack
            //#endif
        );
    }

    @SubscribeEvent
    public void mouseClicked(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (Mouse.getEventButtonState()) {
            event.setCanceled(handleMouseClick(UMouse.getScaledX(), UMouse.getScaledY()));
        }
    }

    @SubscribeEvent
    public void keyInput(GuiScreenEvent.KeyboardInputEvent.Pre event) {
        char typedChar = Keyboard.getEventCharacter();
        int keyCode = Keyboard.getEventKey();
        if ((keyCode != 0 || typedChar < ' ') && !Keyboard.getEventKeyState()) {
            return;
        }

        // Bypass our event for special keys handled by `Minecraft.dispatchKeypresses`
        Minecraft mc = Minecraft.getMinecraft();
        int keyBindCode = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();
        //#if MC>=11200
        if (mc.gameSettings.keyBindFullscreen.isActiveAndMatches(keyBindCode)) {
            return;
        } else if (mc.gameSettings.keyBindScreenshot.isActiveAndMatches(keyBindCode)) {
            return;
        } else if (keyBindCode == Keyboard.KEY_B && GuiScreen.isCtrlKeyDown() && (mc.currentScreen == null || !mc.currentScreen.isFocused())) {
            return;
        }
        //#else
        //$$ if (keyBindCode == mc.gameSettings.keyBindStreamStartStop.getKeyCode()) {
        //$$     return;
        //$$ } else if (keyBindCode == mc.gameSettings.keyBindStreamPauseUnpause.getKeyCode()) {
        //$$     return;
        //$$ } else if (keyBindCode == mc.gameSettings.keyBindStreamCommercials.getKeyCode()) {
        //$$     return;
        //$$ } else if (keyBindCode == mc.gameSettings.keyBindStreamToggleMic.getKeyCode()) {
        //$$     return;
        //$$ } else if (keyBindCode == mc.gameSettings.keyBindFullscreen.getKeyCode()) {
        //$$     return;
        //$$ } else if (keyBindCode == mc.gameSettings.keyBindScreenshot.getKeyCode()) {
        //$$     return;
        //$$ }
        //#endif

        event.setCanceled(handleKeyTyped(keyCode));
    }
    //#endif
    //#endif

    public static class DrawEvent {
        public int mouseX;
        public int mouseY;
        int originalMouseX;
        int originalMouseY;

        public DrawEvent(int mouseX, int mouseY) {
            this.mouseX = mouseX;
            this.mouseY = mouseY;
            this.originalMouseX = mouseX;
            this.originalMouseY = mouseY;
        }

        public boolean mouseXChanged() {
            return this.mouseX != this.originalMouseX;
        }

        public boolean mouseYChanged() {
            return this.mouseY != this.originalMouseY;
        }
    }

    public static class MousePosition {
        public final double mouseX;
        public final double mouseY;
        public final double originalMouseX;
        public final double originalMouseY;

        public MousePosition(double mouseX, double mouseY, double originalMouseX, double originalMouseY) {
            this.mouseX = mouseX;
            this.mouseY = mouseY;
            this.originalMouseX = originalMouseX;
            this.originalMouseY = originalMouseY;
        }
    }

    private static class GlobalMouseOverride {
        //#if MC>=11600
        //$$ public static void set(double mouseX, double mouseY) {
        //$$     MouseHelperAccessor accessor = (MouseHelperAccessor) Minecraft.getInstance().mouseHelper;
        //$$     accessor.setMouseX(mouseX);
        //$$     accessor.setMouseY(mouseY);
        //$$ }
        //#else
        private static final MethodHandle xField;
        private static final MethodHandle yField;
        private static final MethodHandle eventXField;
        private static final MethodHandle eventYField;
        static {
            try {
                Class<?> cls = Mouse.class;
                MethodHandles.Lookup lookup = MethodHandles.lookup();

                xField = unreflect(lookup, cls, "x");
                yField = unreflect(lookup, cls, "y");
                eventXField = unreflect(lookup, cls, "event_x");
                eventYField = unreflect(lookup, cls, "event_y");
            } catch (Exception e) {
                throw new RuntimeException("Failed to setup global mouse override", e);
            }
        }

        private static MethodHandle unreflect(MethodHandles.Lookup lookup, Class<?> cls, String name) throws NoSuchFieldException, IllegalAccessException {
            Field f = cls.getDeclaredField(name);
            f.setAccessible(true);
            return lookup.unreflectSetter(f);
        }

        public static void set(double mouseX, double mouseY) {
            int trueX = (int) mouseX;
            int trueY = Minecraft.getMinecraft().displayHeight - (int) mouseY - 1;
            try {
                xField.invokeExact(trueX);
                yField.invokeExact(trueY);
                eventXField.invokeExact(trueX);
                eventYField.invokeExact(trueY);
            } catch (Throwable t) {
                throw new RuntimeException("Failed to override mouse position", t);
            }
        }
        //#endif
    }

}

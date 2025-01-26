package gg.essential.ad.mixins;

import gg.essential.ad.modal.ModalManager;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Screen.class)
public class Mixin_Screen_OverrideMousePosition {

    // FIXME these technically aren't correct, since we don't transform from raw to mc coordinates,
    //       but that doesn't matter for the FAKE_MOUSE_POS constant

    @ModifyVariable(method = "render", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    public int modifyMouseX(int mouseX) {
        ModalManager.MousePosition pos = ModalManager.INSTANCE.getMousePosition();
        if (pos != null) {
            return (int) pos.mouseX;
        }
        return mouseX;
    }

    @ModifyVariable(method = "render", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    public int modifyMouseY(int mouseY) {
        ModalManager.MousePosition pos = ModalManager.INSTANCE.getMousePosition();
        if (pos != null) {
            return (int) pos.mouseY;
        }
        return mouseY;
    }
}

package gg.essential.ad.mixins;

import gg.essential.ad.EssentialPartner;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class Mixin_Init {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void initEssentialPartner(CallbackInfo ci) {
        new EssentialPartner();
    }
}

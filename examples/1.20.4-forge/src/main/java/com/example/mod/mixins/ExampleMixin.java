package com.example.mod.mixins;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class ExampleMixin {
    @Inject(method = "run", at = @At("HEAD"))
    private void exampleInject(CallbackInfo ci) {
        System.out.println("Hello World from Example Mixin");
    }
}

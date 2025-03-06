package gg.essential.partnermod.mixins;

import net.minecraft.client.MouseHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MouseHelper.class)
public interface MouseHelperAccessor {
    @Accessor
    void setMouseX(double value);
    @Accessor
    void setMouseY(double value);
}

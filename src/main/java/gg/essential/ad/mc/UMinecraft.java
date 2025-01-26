package gg.essential.ad.mc;

import net.minecraft.util.ResourceLocation;

public class UMinecraft {

    public static ResourceLocation identifier(String namespace, String path) {
        //#if MC>=12100
        //$$ return Identifier.of(namespace, path);
        //#else
        return new ResourceLocation(namespace, path);
        //#endif
    }

}

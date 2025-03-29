package gg.essential.partnermod;

import gg.essential.partnermod.mc.UMinecraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;

//#if MC>=11600
//$$ import net.minecraft.client.renderer.texture.NativeImage;
//#else
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
//#endif


public class Resources {

    private static final ResourceLocation MISSINGNO = UMinecraft.identifier("minecraft", "missingno");

    private static final AtomicInteger counter = new AtomicInteger(0);

    public static ResourceLocation load(String name) {
        try (InputStream stream = EssentialPartner.class.getResourceAsStream("assets/" + name)) {
            if (stream == null) throw new RuntimeException("Texture not found " + name);
            try (BufferedInputStream bis = new BufferedInputStream(stream)) {
                return load(bis);
            }
        } catch (Exception e) {
            EssentialPartner.LOGGER.error("Failed to load texture {}", name, e);
            return MISSINGNO;
        }
    }

    public static ResourceLocation load(InputStream is) {
        //#if MC>=11600
        //$$ NativeImage image;
        //$$ try {
        //$$     image = NativeImage.read(is);
        //$$ } catch (IOException e) {
        //$$     EssentialPartner.LOGGER.error("Failed to load texture", e);
        //$$     return MISSINGNO;
        //$$ }
        //#else
        BufferedImage image;
        try {
            image = ImageIO.read(is);
        } catch (IOException e) {
            EssentialPartner.LOGGER.error("Failed to load texture", e);
            return MISSINGNO;
        }
        //#endif
        ResourceLocation location = UMinecraft.identifier("essentialad", "texture/" + counter.getAndIncrement());
        //#if MC>=12105
        //$$ NativeImageBackedTexture texture = new NativeImageBackedTexture(location::toString, image);
        //#else
        DynamicTexture texture = new DynamicTexture(image);
        //#endif
        Minecraft.getMinecraft().getTextureManager().loadTexture(location, texture);
        return location;
    }

}

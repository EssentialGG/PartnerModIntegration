package gg.essential.ad;

import gg.essential.ad.mc.UMinecraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

//#if MC>=11600
//$$ import net.minecraft.client.renderer.texture.NativeImage;
//#else
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicInteger;
//#endif


public class Resources {

    private static final ResourceLocation MISSINGNO = UMinecraft.identifier("minecraft", "missingno");

    private static final AtomicInteger counter = new AtomicInteger(0);

    public static ResourceLocation load(String name) {
        try (InputStream stream = EssentialAd.class.getResourceAsStream("assets/" + name)) {
            if (stream == null) throw new RuntimeException("Texture not found " + name);
            try (BufferedInputStream bis = new BufferedInputStream(stream)) {
                return load(bis);
            }
        } catch (Exception e) {
            EssentialAd.LOGGER.error("Failed to load texture {}", name, e);
            return MISSINGNO;
        }
    }

    public static ResourceLocation load(InputStream is) {
        //#if MC>=11600
        //$$ NativeImage image;
        //$$ try {
        //$$     image = NativeImage.read(is);
        //$$ } catch (IOException e) {
        //$$     EssentialAd.LOGGER.error("Failed to load texture", e);
        //$$     return MISSINGNO;
        //$$ }
        //#else
        BufferedImage image;
        try {
            image = ImageIO.read(is);
        } catch (IOException e) {
            EssentialAd.LOGGER.error("Failed to load texture", e);
            return MISSINGNO;
        }
        //#endif
        DynamicTexture texture = new DynamicTexture(image);
        ResourceLocation location = UMinecraft.identifier("essentialad", "texture/" + counter.getAndIncrement());
        Minecraft.getMinecraft().getTextureManager().loadTexture(location, texture);
        return location;
    }

}

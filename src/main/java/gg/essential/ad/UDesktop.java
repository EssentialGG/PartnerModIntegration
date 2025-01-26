package gg.essential.ad;

import java.net.URI;

//#if MC>=11600
//$$ import net.minecraft.util.Util;
//#else
import java.awt.Desktop;
//#endif

public class UDesktop {

    public static void browse(String uri) {
        try {
            //#if MC>=11600
            //$$ Util.getOSType().openURI(uri);
            //#else
            Desktop.getDesktop().browse(URI.create(uri));
            //#endif
        } catch (Exception e) {
            EssentialAd.LOGGER.error("Failed to browse to {}", uri, e);
        }
    }

}

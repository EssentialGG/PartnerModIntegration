package gg.essential.ad;

import net.minecraft.client.Minecraft;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class EssentialUtil {

    private static boolean installationCompleted = false;
    private static Boolean isEssentiaLoaded = null;

    public static boolean isEssentialLoaded() {
        if (isEssentiaLoaded == null) {
            isEssentiaLoaded = ModLoaderUtil.isModLoaded("essential");
        }
        return isEssentiaLoaded;
    }

    public static boolean installationCompleted() {
        return installationCompleted;
    }

    public static boolean installContainer() {
        try {
            Path destination = Paths.get("mods", "essential-container.jar");
            if (Files.exists(destination)) throw new RuntimeException("fix me!"); // fixme filename conflicts
            try (InputStream is = EssentialUtil.class.getResourceAsStream("container.jar")) {
                if (is == null) throw new IllegalStateException("No bundled container jar found!");
                Files.copy(is, destination);
            }
            EssentialAd.LOGGER.info("Successfully installed essential container to {}", destination.toRealPath());
            installationCompleted = true;
            return true;
        } catch (Exception e) {
            EssentialAd.LOGGER.error("Failed to install essential container", e);
            return false;
        }
    }

    public static void shutdown() {
        Minecraft.getMinecraft().shutdown();
    }

}

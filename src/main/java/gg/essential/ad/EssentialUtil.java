package gg.essential.ad;

import net.minecraft.client.Minecraft;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class EssentialUtil {

    private static boolean installationCompleted = false;
    private static Boolean isEssentialLoaded = null;

    public static boolean isEssentialOrContainerLoaded() {
        if (isEssentialLoaded == null) {
            isEssentialLoaded = ModLoaderUtil.isModLoaded("essential") || isEssentialContainerLoaded();
        }
        return isEssentialLoaded;
    }

    // From: https://github.com/EssentialGG/EssentialDev/blob/8c17244b8ad2e4058578487a16018b17376ba150/src/main/java/gg/essential/util/EssentialContainerUtil.java#L18-L31
    private static boolean isEssentialContainerLoaded() {
        //#if FABRIC
        //$$ return net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("essential-container");
        //#elseif MC>=11700
        //$$ return cpw.mods.modlauncher.Launcher.INSTANCE.findLayerManager()
        //$$     .flatMap(it -> it.getLayer(cpw.mods.modlauncher.api.IModuleLayerManager.Layer.SERVICE))
        //$$     .map(ModuleLayer::modules)
        //$$     .orElseGet(java.util.Collections::emptySet)
        //$$     .stream()
        //$$     .anyMatch(it -> it.getClassLoader().getResource("essential_container_marker.txt") != null);
        //#else
        return EssentialUtil.class.getClassLoader().getResource("essential_container_marker.txt") != null;
        //#endif
    }

    public static boolean installationCompleted() {
        return installationCompleted;
    }

    public static boolean installContainer() {
        try {
            Path destination = Paths.get("mods", "essential-container.jar");
            if (Files.exists(destination)) {
                int i = 1;
                do {
                    destination = Paths.get("mods", "essential-container-" + i + ".jar");
                    i++;
                } while (Files.exists(destination));
            }
            try (InputStream is = EssentialUtil.class.getResourceAsStream("container.jarx")) {
                if (is == null) throw new IllegalStateException("No bundled container jar found!");
                Files.copy(is, destination);
            }
            EssentialPartner.LOGGER.info("Successfully installed essential container to {}", destination.toRealPath());
            installationCompleted = true;
            return true;
        } catch (Exception e) {
            EssentialPartner.LOGGER.error("Failed to install essential container", e);
            return false;
        }
    }

    public static void shutdown() {
        Minecraft.getMinecraft().shutdown();
    }

}

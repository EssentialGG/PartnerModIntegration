/*
 * Copyright © 2025 ModCore Inc. All rights reserved.
 *
 * This code is part of ModCore Inc.’s Essential Partner Mod Integration
 * repository and is protected under copyright. For the full license, see:
 * https://github.com/EssentialGG/EssentialPartnerMod/tree/main/LICENSE
 *
 * You may modify, fork, and use the Mod, but may not retain ownership of
 * accepted contributions, claim joint ownership, or use Essential’s trademarks.
 */

package gg.essential.partnermod;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import gg.essential.partnermod.data.PartnerModData;
import net.minecraft.client.Minecraft;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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

    public static boolean installContainer(List<PartnerModData.PartnerMod> partnerMods) {
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
            writeMetadataFile(partnerMods);
            return true;
        } catch (Exception e) {
            EssentialPartner.LOGGER.error("Failed to install essential container", e);
            return false;
        }
    }

    private static void writeMetadataFile(List<PartnerModData.PartnerMod> partnerMods) {
        Path metadataDestination = Paths.get("essential", "partner-integration-mod-metadata.json");
        try {
            Files.createDirectories(metadataDestination.getParent());
            JsonObject metadata = new JsonObject();
            JsonArray partnerArray = new JsonArray();
            for (PartnerModData.PartnerMod partnerMod : partnerMods) {
                partnerArray.add(new JsonPrimitive(partnerMod.getId()));
            }
            metadata.add("partnerMods", partnerArray);
            Files.write(metadataDestination, metadata.toString().getBytes(StandardCharsets.UTF_8));
            EssentialPartner.LOGGER.info("Successfully saved telemetry file to {}", metadataDestination.toRealPath());
        } catch (Exception e) {
            EssentialPartner.LOGGER.warn("Failed to store telemetry file to {}", metadataDestination.toAbsolutePath(), e);
        }
    }

    public static void shutdown() {
        Minecraft.getMinecraft().shutdown();
    }

}

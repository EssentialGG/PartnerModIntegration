package gg.essential.partnermod;

import gg.essential.partnermod.data.PartnerModData;
import gg.essential.partnermod.data.ModalData;
import gg.essential.partnermod.loader.EssentialPartnerLoader;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class EssentialAPI {

    private static final String API_BASE_URL = System.getProperty(
        "essential.partnermod.api",
        "https://api.essential.gg"
    );
    private static final String MOD_PARTNER_DATA_URL = API_BASE_URL + "/v1/mod-partner";

    private static final String USER_AGENT = "EssentialPartnerMod/" + EssentialPartnerLoader.OUR_VERSION + " (" + EssentialPartnerLoader.OUR_PKG + ")";

    private static final Path API_CACHE_FILE = PartnerModConfig.CONFIG_FOLDER.resolve("data.cache.json");

    private static final Path API_OVERRIDE_FILE = PartnerModConfig.CONFIG_FOLDER.resolve("data.override.json");
    private static final Path MODAL_OVERRIDE_FOLDER = PartnerModConfig.CONFIG_FOLDER.resolve("override");
    private static final Path MODAL_OVERRIDE_FILE = MODAL_OVERRIDE_FOLDER.resolve("mod-partner-modal-metadata.json");

    public static CompletableFuture<PartnerModData> fetchPartnerModData() {
        CompletableFuture<PartnerModData> future = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            try {
                if (Files.exists(API_OVERRIDE_FILE)) {
                    EssentialPartner.LOGGER.info("Using API override file");
                    try (BufferedReader reader = Files.newBufferedReader(API_OVERRIDE_FILE)) {
                        PartnerModData data = EssentialPartner.GSON.fromJson(reader, PartnerModData.class);
                        future.complete(data);
                        return;
                    } catch (Exception e) {
                        EssentialPartner.LOGGER.error("Failed to load api override", e);
                    }
                }

                HttpURLConnection connection = (HttpURLConnection) URI.create(MOD_PARTNER_DATA_URL).toURL().openConnection();
                connection.setConnectTimeout(10_000);
                connection.setReadTimeout(10_000);
                connection.addRequestProperty("User-Agent", USER_AGENT);

                connection.connect();

                String response;
                try (InputStream is = connection.getInputStream()) {
                    response = IOUtils.toString(is, StandardCharsets.UTF_8);
                }

                try (BufferedWriter writer = Files.newBufferedWriter(API_CACHE_FILE)) {
                    writer.write(response);
                } catch (Exception e) {
                    EssentialPartner.LOGGER.error("Failed to write cached response", e);
                }

                PartnerModData data = EssentialPartner.GSON.fromJson(response, PartnerModData.class);

                if (Files.exists(MODAL_OVERRIDE_FILE)) {
                    EssentialPartner.LOGGER.info("Using modal override folder");
                    try (BufferedReader reader = Files.newBufferedReader(MODAL_OVERRIDE_FILE)) {
                        ModalData modalData = EssentialPartner.GSON.fromJson(reader, ModalData.class);
                        // Replace image paths with base64 representation
                        for (ModalData.Feature feature : modalData.getFeatures()) {
                            Path iconPath = MODAL_OVERRIDE_FOLDER.resolve(feature.getIcon());
                            try {
                                byte[] bytes = Files.readAllBytes(iconPath);
                                String base64 = Base64.getEncoder().encodeToString(bytes);
                                feature.setIcon(base64);
                            } catch (IOException e) {
                                EssentialPartner.LOGGER.error("Failed to load icon {}", iconPath, e);
                            }
                        }

                        data = new PartnerModData(modalData, data.getPartneredMods());
                    } catch (Exception e) {
                        EssentialPartner.LOGGER.error("Failed to load modal override", e);
                    }
                }

                future.complete(data);
            } catch (Exception e) {
                EssentialPartner.LOGGER.error("Failed to fetch modal data", e);

                try {
                    future.complete(getFallbackData());
                } catch (Exception e2) {
                    EssentialPartner.LOGGER.error("Failed to load fallback modal data", e2);
                    future.completeExceptionally(e2);
                }
            }
        });
        return future;
    }

    private static PartnerModData getFallbackData() throws IOException {
        if (Files.exists(API_CACHE_FILE)) {
            try (BufferedReader reader = Files.newBufferedReader(API_CACHE_FILE)) {
                PartnerModData data = EssentialPartner.GSON.fromJson(reader, PartnerModData.class);
                EssentialPartner.LOGGER.info("Loaded cached response");
                return data;
            } catch (Exception e) {
                EssentialPartner.LOGGER.error("Failed to load cached response", e);
            }
        }

        try (InputStream is = EssentialPartner.class.getResourceAsStream("assets/data.fallback.json")) {
            Objects.requireNonNull(is, "Fallback data missing!");
            PartnerModData data = EssentialPartner.GSON.fromJson(IOUtils.toString(is, StandardCharsets.UTF_8), PartnerModData.class);
            EssentialPartner.LOGGER.info("Loaded fallback data");
            return data;
        }
    }
}

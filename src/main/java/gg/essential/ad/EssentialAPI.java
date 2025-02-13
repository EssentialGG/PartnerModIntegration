package gg.essential.ad;

import gg.essential.ad.data.AdData;
import gg.essential.ad.data.ModalData;
import gg.essential.ad.loader.EssentialAdLoader;
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
        "essential.ad.api",
        "https://api.essential.gg"
    );
    private static final String AD_DATA_URL = API_BASE_URL + "/v1/mod-partner";

    private static final String USER_AGENT = "EssentialAd/" + EssentialAdLoader.OUR_VERSION + " (" + EssentialAdLoader.OUR_PKG + ")";

    private static final Path API_CACHE_FILE = AdConfig.CONFIG_FOLDER.resolve("data.cache.json");

    private static final Path API_OVERRIDE_FILE = AdConfig.CONFIG_FOLDER.resolve("data.override.json");
    private static final Path MODAL_OVERRIDE_FOLDER = AdConfig.CONFIG_FOLDER.resolve("override");
    private static final Path MODAL_OVERRIDE_FILE = MODAL_OVERRIDE_FOLDER.resolve("mod-partner-modal-metadata.json");

    public static CompletableFuture<AdData> fetchAdData() {
        CompletableFuture<AdData> future = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            try {
                if (Files.exists(API_OVERRIDE_FILE)) {
                    EssentialAd.LOGGER.info("Using API override file");
                    try (BufferedReader reader = Files.newBufferedReader(API_OVERRIDE_FILE)) {
                        AdData data = EssentialAd.GSON.fromJson(reader, AdData.class);
                        future.complete(data);
                        return;
                    } catch (Exception e) {
                        EssentialAd.LOGGER.error("Failed to load api override", e);
                    }
                }

                HttpURLConnection connection = (HttpURLConnection) URI.create(AD_DATA_URL).toURL().openConnection();
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
                    EssentialAd.LOGGER.error("Failed to write cached response", e);
                }

                AdData data = EssentialAd.GSON.fromJson(response, AdData.class);

                if (Files.exists(MODAL_OVERRIDE_FILE)) {
                    EssentialAd.LOGGER.info("Using modal override folder");
                    try (BufferedReader reader = Files.newBufferedReader(MODAL_OVERRIDE_FILE)) {
                        ModalData modalData = EssentialAd.GSON.fromJson(reader, ModalData.class);
                        // Replace image paths with base64 representation
                        for (ModalData.Feature feature : modalData.getFeatures()) {
                            Path iconPath = MODAL_OVERRIDE_FOLDER.resolve(feature.getIcon());
                            try {
                                byte[] bytes = Files.readAllBytes(iconPath);
                                String base64 = Base64.getEncoder().encodeToString(bytes);
                                feature.setIcon(base64);
                            } catch (IOException e) {
                                EssentialAd.LOGGER.error("Failed to load icon {}", iconPath, e);
                            }
                        }

                        data = new AdData(modalData, data.getPartneredMods());
                    } catch (Exception e) {
                        EssentialAd.LOGGER.error("Failed to load modal override", e);
                    }
                }

                future.complete(data);
            } catch (Exception e) {
                EssentialAd.LOGGER.error("Failed to fetch modal data", e);

                try {
                    future.complete(getFallbackData());
                } catch (Exception e2) {
                    EssentialAd.LOGGER.error("Failed to load fallback modal data", e2);
                    future.completeExceptionally(e2);
                }
            }
        });
        return future;
    }

    private static AdData getFallbackData() throws IOException {
        if (Files.exists(API_CACHE_FILE)) {
            try (BufferedReader reader = Files.newBufferedReader(API_CACHE_FILE)) {
                AdData data = EssentialAd.GSON.fromJson(reader, AdData.class);
                EssentialAd.LOGGER.info("Loaded cached response");
                return data;
            } catch (Exception e) {
                EssentialAd.LOGGER.error("Failed to load cached response", e);
            }
        }

        try (InputStream is = EssentialAd.class.getResourceAsStream("assets/data.fallback.json")) {
            Objects.requireNonNull(is, "Fallback data missing!");
            AdData data = EssentialAd.GSON.fromJson(IOUtils.toString(is, StandardCharsets.UTF_8), AdData.class);
            EssentialAd.LOGGER.info("Loaded fallback data");
            return data;
        }
    }
}

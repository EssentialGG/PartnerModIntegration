package gg.essential.ad;

import gg.essential.ad.data.AdData;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class EssentialAPI {

    private static final String API_BASE_URL = System.getProperty(
        "essential.ad.api",
        "https://api.lon-dev.modcore.dev" // fixme replace with prod
    );
    private static final String AD_DATA_URL = API_BASE_URL + "/v1/mod-partner";

    private static final String USER_AGENT = "Essential Ad (1.0.0)"; // fixme dynamic version

    private static final Path OVERRIDE_FILE = AdConfig.CONFIG_FOLDER.resolve("data.override.json");

    public static CompletableFuture<AdData> fetchAdData() {
        CompletableFuture<AdData> future = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            try {
                // todo: add an override file just for the modal, and that behaves like that configuration repo (images as separate files)
                if (Files.exists(OVERRIDE_FILE)) {
                    try (BufferedReader reader = Files.newBufferedReader(OVERRIDE_FILE)) {
                        AdData data = EssentialAd.GSON.fromJson(reader, AdData.class);
                        future.complete(data);
                        return;
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

                AdData data = EssentialAd.GSON.fromJson(response, AdData.class);
                future.complete(data);
            } catch (Exception e) {
                EssentialAd.LOGGER.error("Failed to fetch modal data", e);
                future.completeExceptionally(e);
            }
        });
        return future;
    }
}

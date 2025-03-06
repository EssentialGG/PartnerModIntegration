package gg.essential.partnermod;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PartnerModConfig {

    public static final Path CONFIG_FOLDER = Paths.get("config", "essential-mod-partner");
    private static final Path CONFIG_FILE = CONFIG_FOLDER.resolve("config.json");
    private static final Path HIDE_BUTTONS_FILE = CONFIG_FOLDER.resolve("hide_buttons.txt");

    private boolean buttonsHidden;
    private final transient boolean buttonsHiddenByFile = Files.exists(HIDE_BUTTONS_FILE);

    public static PartnerModConfig load() {
        if (Files.exists(CONFIG_FILE)) {
            try (BufferedReader reader = Files.newBufferedReader(CONFIG_FILE)) {
                return EssentialPartner.GSON.fromJson(reader, PartnerModConfig.class);
            } catch (Exception e) {
                EssentialPartner.LOGGER.error("Failed to read config", e);
            }
        }
        PartnerModConfig config = new PartnerModConfig();
        config.write();
        return config;
    }

    public void write() {
        try {
            if (!Files.exists(CONFIG_FOLDER)) {
                Files.createDirectories(CONFIG_FOLDER);
            }
            Files.write(CONFIG_FILE, EssentialPartner.GSON.toJson(this).getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            EssentialPartner.LOGGER.info("Failed to write config", e);
        }
    }

    public boolean shouldHideButtons() {
        return buttonsHidden || buttonsHiddenByFile;
    }

    public void hideButtons() {
        buttonsHidden = true;
        write();
    }

}

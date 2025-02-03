package gg.essential.ad.loader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

//#if MC>=11400
//$$ import org.spongepowered.asm.service.IGlobalPropertyService;
//$$ import org.spongepowered.asm.service.MixinService;
//#else
import net.minecraft.launchwrapper.Launch;
//#endif

/**
 * On some mod loaders there may be multiple relocated instances of this code.
 * This class coordinates between them to determine the most recent one allowing all other instances to remain inactive.
 * For this negotiation to work, {@link #propose()} has to be called as early as possible, while {@link #isActive()}
 * must be called as late as possible. More specifically, all {@link #propose()} calls must happen before any
 * {@link #isActive()} calls.
 */
public class EssentialAdLoader {
    private static final boolean VERBOSE = Boolean.getBoolean("essentialad.loader.verbose");
    private static final Logger LOGGER = LogManager.getLogger();

    public static final String OUR_PKG;
    static {
        String name = EssentialAdLoader.class.getName();
        OUR_PKG = name.substring(0, name.length() - ".loader.EssentialAdLoader".length());
    }

    // Using `replace` to prevent the shadow plugin from relocating this string constant
    private static final String COMMON_PACKAGE = "gg;essential;ad".replace(";", ".");
    private static final String PROPOSED_VERSION_KEY = COMMON_PACKAGE + ".proposedVersion";
    private static final String ACTIVE_VERSION_KEY = COMMON_PACKAGE + ".activeVersion";

    public static final String OUR_VERSION;
    static {
        try (InputStream in = EssentialAdLoader.class.getResourceAsStream("version.txt")) {
            if  (in == null) throw new RuntimeException("Failed to find version.txt");
            OUR_VERSION = new BufferedReader(new InputStreamReader(in)).readLine().trim();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Boolean isActive;

    public static void propose() {
        synchronized (System.class) {
            String activeVersion = blackboardGet(ACTIVE_VERSION_KEY);
            if (activeVersion != null) {
                throw new IllegalStateException("Tried to load version " + OUR_VERSION + " but version " + activeVersion + " is already active!");
            }

            String proposedVersion = blackboardGet(PROPOSED_VERSION_KEY);
            if (proposedVersion == null || compareVersions(proposedVersion, OUR_VERSION) < 0) {
                if (VERBOSE) LOGGER.info("{} proposing {}", OUR_PKG, OUR_VERSION);
                blackboardSet(PROPOSED_VERSION_KEY, OUR_VERSION);
            } else {
                if (VERBOSE) LOGGER.info("{} version is {}, current proposed is already {}", OUR_PKG, OUR_VERSION, proposedVersion);
            }
        }
    }

    public static boolean isActive() {
        if (isActive != null) {
            return isActive;
        }

        synchronized (System.class) {
            if (isActive != null) {
                return isActive;
            }

            String activeVersion = blackboardGet(ACTIVE_VERSION_KEY);
            if (activeVersion != null) {
                if (VERBOSE) LOGGER.info("{} (version {}) is inactive because {} is already active", OUR_PKG, OUR_VERSION, activeVersion);
                return isActive = false; // another instance is already active
            }

            String proposedVersion = blackboardGet(PROPOSED_VERSION_KEY);
            if (!OUR_VERSION.equals(proposedVersion)) {
                if (VERBOSE) LOGGER.info("{} (version {}) is inactive because newer {} has been proposed", OUR_PKG, OUR_VERSION, proposedVersion);
                return isActive = false; // a newer version is available
            }

            // Our version is the most recent one, so we'll be active
            if (VERBOSE) LOGGER.info("{} (version {}) is now active", OUR_PKG, OUR_VERSION);
            blackboardSet(ACTIVE_VERSION_KEY, proposedVersion);
            return isActive = true;
        }
    }

    private static String blackboardGet(String key) {
        //#if MC>=11400
        //$$ IGlobalPropertyService service = MixinService.getGlobalPropertyService();
        //$$ Object[] container = service.getProperty(service.resolveKey(key));
        //$$ return container == null ? null : (String) container[0];
        //#else
        return (String) Launch.blackboard.get(key);
        //#endif
    }

    private static void blackboardSet(String key, String value) {
        //#if MC>=11400
        //$$ IGlobalPropertyService service = MixinService.getGlobalPropertyService();
        //$$ // Using single-value array as container for our value because on ModLauncher properties cannot be modified
        //$$ // once written
        //$$ Object[] container = service.getProperty(service.resolveKey(key));
        //$$ if (container == null) {
        //$$     service.setProperty(service.resolveKey(key), container = new Object[1]);
        //$$ }
        //$$ container[0] = value;
        //#else
        Launch.blackboard.put(key, value);
        //#endif
    }

    // Borrowed from https://github.com/EssentialGG/EssentialLoader/blob/f75128ab18b82f473be64bab82499ef1a398ab55/stage1/common/src/main/java/gg/essential/loader/stage1/VersionComparison.java
    // where it also has tests.
    private static int compareVersions(String a, String b) {
        int aPlusIndex = a.indexOf('+');
        int bPlusIndex = b.indexOf('+');
        if (aPlusIndex == -1) aPlusIndex = a.length();
        if (bPlusIndex == -1) bPlusIndex = b.length();
        String[] aParts = a.substring(0, aPlusIndex).replace('-', '.').split("\\.");
        String[] bParts = b.substring(0, bPlusIndex).replace('-', '.').split("\\.");
        for (int i = 0; i < Math.max(aParts.length, bParts.length); i++) {
            String aPart = i < aParts.length ? aParts[i] : "0";
            String bPart = i < bParts.length ? bParts[i] : "0";
            Integer aInt = toIntOrNull(aPart);
            Integer bInt = toIntOrNull(bPart);
            int compare;
            if (aInt != null && bInt != null) {
                // Both numbers, compare numerically
                compare = Integer.compare(aInt, bInt);
            } else if (aInt == null && bInt == null) {
                // Both non-numbers, compare lexicographically
                compare = aPart.compareTo(bPart);
            } else {
                // Mixed, number wins over str, e.g. 1.0.0.1 is greater than 1.0.0-rc.1
                if (aInt != null) {
                    compare = 1;
                } else {
                    compare = -1;
                }
            }
            if (compare != 0) {
                return compare;
            }
        }
        return 0;
    }

    private static Integer toIntOrNull(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

package gg.essential.partnermod;

import java.io.IOException;
import java.net.URI;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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
            if (Desktop.isDesktopSupported()) {
                try {
                    if (Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                        Desktop.getDesktop().browse(URI.create(uri));
                        return;
                    }
                } catch (Throwable t) {
                    EssentialPartner.LOGGER.error("Failed to browse via AWT to {}", uri, t);
                }
            }
            String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);
            if (os.contains("windows")) {
                runCommand(false, "rundll32", "url.dll,FileProtocolHandler", uri);
            } else if (os.contains("mac") || os.contains("darwin")) {
                runCommand(false, "open", uri);
            } else if (os.contains("linux")) {
                for (String command : new String[]{"xdg-open", "kde-open", "gnome-open"}) {
                    if (runCommand(true, command, uri)) {
                        return;
                    }
                }
            } else {
                throw new UnsupportedOperationException("Unsupported OS `" + os + "`");
            }
            //#endif
        } catch (Exception e) {
            EssentialPartner.LOGGER.error("Failed to browse to {}", uri, e);
        }
    }

    /**
     * Runs the given command with arguments via [Runtime.exec].
     * <p>
     * If [checkExitStatus] is true, the method will wait for the process to exit (but at most a few seconds) and then
     * return `false` if the process exit code is non-zero (`true` if the process did not exit in time).
     */
    private static boolean runCommand(boolean checkExitStatus, String... command) throws InterruptedException {
        try {
            Process process = Runtime.getRuntime().exec(command);
            if (process == null) {
                return false;
            }
            if (checkExitStatus) {
                if (process.waitFor(3, TimeUnit.SECONDS)) {
                    return process.exitValue() == 0;
                } else {
                    return true; // still running, assume success
                }
            } else {
                return process.isAlive();
            }
        } catch (IOException e) {
            return false;
        }
    }
}

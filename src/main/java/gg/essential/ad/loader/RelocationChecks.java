package gg.essential.ad.loader;

public class RelocationChecks {
    public static void verifyRelocation() {
        if (isDevelopment() || isStandalone()) {
            return;
        }

        // Using `replace` to prevent the shadow plugin from relocating this string constant
        String originalPackage = "gg;essential;ad".replace(";", ".");
        String actualPackage = RelocationChecks.class.getName();
        actualPackage = actualPackage.substring(0, actualPackage.length() - ".loader.RelocationChecks".length());

        // Ensure classes have been relocated
        //noinspection ConstantValue
        if (actualPackage.equals(originalPackage)) error();
        // Ensure resources have been relocated
        if (RelocationChecks.class.getResource("/" + actualPackage.replace('.', '/') + "/container.jarx") == null) error();
        // Ensure mod class has been excluded
        if (RelocationChecks.class.getResource("/" + actualPackage.replace('.', '/') + "/EssentialAdMod.class") != null) error();
    }

    private static boolean isDevelopment() {
        //#if FABRIC
        //$$ return net.fabricmc.loader.api.FabricLoader.getInstance().isDevelopmentEnvironment();
        //#else
        //#if MC>=11400
        //$$ String target = System.getenv("target");
        //$$ if (target == null) return false;
        //$$ return target.equalsIgnoreCase("fmluserdevclient");
        //#else
        return net.minecraft.launchwrapper.Launch.blackboard.get("fml.deobfuscatedEnvironment") == Boolean.TRUE;
        //#endif
        //#endif
    }

    private static boolean isStandalone() {
        Package pkg = RelocationChecks.class.getPackage();
        if (pkg == null) return false;
        return "EssentialAd".equals(pkg.getImplementationTitle()) && "ModCore Inc.".equals(pkg.getImplementationVendor());
    }

    private static void error() {
        throw new RuntimeException("The Essential Ad mod has not been relocated properly! Please check the README.");
    }
}

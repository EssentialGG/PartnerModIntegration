package gg.essential.partnermod.loader;

public class RelocationChecks {
    public static void verifyRelocation() {
        if (isDevelopment() || isStandalone()) {
            return;
        }

        // Using `replace` to prevent the shadow plugin from relocating this string constant
        String originalPackage = "gg;essential;partnermod".replace(";", ".");
        String actualPackage = RelocationChecks.class.getName();
        actualPackage = actualPackage.substring(0, actualPackage.length() - ".loader.RelocationChecks".length());

        // Ensure classes have been relocated
        //noinspection ConstantValue
        if (actualPackage.equals(originalPackage)) error();
        // Ensure resources have been relocated
        if (RelocationChecks.class.getResource("/" + actualPackage.replace('.', '/') + "/container.jarx") == null) error();
        // Ensure mod class has been excluded
        if (RelocationChecks.class.getResource("/" + actualPackage.replace('.', '/') + "/EssentialPartnerMod.class") != null) error();
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
        //#if FABRIC
        //$$ // FIXME can't get this to work on fabric
        //$$ //  relocation isn't needed on fabric anyway, so let's just assume no one does it
        //$$ return true;
        //#else
        Package pkg = RelocationChecks.class.getPackage();
        if (pkg == null) return false;
        return "EssentialPartnerModIntegration".equals(pkg.getImplementationTitle()) && "ModCore Inc.".equals(pkg.getImplementationVendor());
        //#endif
    }

    private static void error() {
        throw new RuntimeException("The Essential Partner Integration mod has not been relocated properly! Please check the README.");
    }
}

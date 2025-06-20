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

package gg.essential.partnermod.asm;

import gg.essential.partnermod.loader.EssentialPartnerLoader;
import gg.essential.partnermod.loader.RelocationChecks;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.relauncher.CoreModManager;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

public class EssentialPartnerCoreMod implements IFMLLoadingPlugin {
    static {
        loadDelegate();
        EssentialPartnerLoader.propose();
    }

    private static void loadDelegate() {
        try {
            URL url = EssentialPartnerCoreMod.class.getResource("EssentialPartnerCoreMod.class");
            if (url == null) throw new RuntimeException("Failed to find own class file!?");
            URLConnection connection = url.openConnection();
            if (!(connection instanceof JarURLConnection)) {
                return; // development environment
            }
            JarURLConnection jarConnection = (JarURLConnection) connection;
            String delegateName = jarConnection.getMainAttributes().getValue("EssentialPartnerCoreModDelegate");
            if (delegateName == null) {
                return; // user mod has no core mod
            }
            File file = new File(jarConnection.getJarFileURL().toURI());

            // Method exists with this signature and functionality since FML for MC 1.6.2, so should be safe to rely on
            Method method = CoreModManager.class.getDeclaredMethod("loadCoreMod", LaunchClassLoader.class, String.class, File.class);
            method.setAccessible(true);
            method.invoke(null, Launch.classLoader, delegateName, file);
        } catch (IOException | ReflectiveOperationException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String[] getASMTransformerClass() {
        if (EssentialPartnerLoader.isActive()) {
            return new String[]{EssentialPartnerClassTransformer.class.getName()};
        } else {
            return null;
        }
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> map) {
        // Note: Cannot do this in <(c)init> because Forge doesn't set up "fml.deobfuscatedEnvironment" until the next
        //       tweaker round.
        RelocationChecks.verifyRelocation();
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}

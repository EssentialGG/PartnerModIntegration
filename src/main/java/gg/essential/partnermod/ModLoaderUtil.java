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

//#if FABRIC
//$$ import net.fabricmc.loader.api.FabricLoader;
//#else
//#if MC>=11400
//$$ import net.minecraftforge.fml.loading.LoadingModList;
//#else
import net.minecraftforge.fml.common.Loader;
//#endif
//#endif

public class ModLoaderUtil {

    public static boolean isModLoaded(String modId) {
        //#if FABRIC
        //$$ return FabricLoader.getInstance().isModLoaded(modId);
        //#else
        //#if MC>=11400
        //$$ return LoadingModList.get().getModFileById(modId) != null;
        //#else
        return Loader.instance().getIndexedModList().containsKey(modId);
        //#endif
        //#endif
    }

}

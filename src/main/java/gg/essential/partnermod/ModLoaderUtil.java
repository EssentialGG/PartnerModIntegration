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

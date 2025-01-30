package gg.essential.ad.asm;

import gg.essential.ad.loader.EssentialAdLoader;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class EssentialAdCoreMod implements IFMLLoadingPlugin {
    static {
        EssentialAdLoader.propose();
    }

    @Override
    public String[] getASMTransformerClass() {
        if (EssentialAdLoader.isActive()) {
            return new String[]{EssentialAdClassTransformer.class.getName()};
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

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}

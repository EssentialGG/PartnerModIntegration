package gg.essential.ad.asm;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class EssentialAdCoreMod implements IFMLLoadingPlugin {
    @Override
    public String[] getASMTransformerClass() {
        return new String[]{"gg.essential.ad.asm.EssentialAdClassTransformer"};
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

package com.example.mod.asm;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.LdcInsnNode;

import java.util.Map;

// Example core mod to test that delegation works as expected; if you don't know what this is, you don't need it
public class ExampleModCoreMod implements IFMLLoadingPlugin {
    {
        System.out.println("Hello World from Example Mod Core Mod");
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{"com.example.mod.asm.ExampleModClassTransformer"};
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

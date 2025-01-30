package com.example.mod;

import net.minecraftforge.fml.common.Mod;

@Mod(modid = "examplemod")
public class ExampleMod {
    {
        System.out.println("Hello World from Example Mod!");
    }

    public static void transformerHello() {
        System.out.println("Hello World from Example Mod Class Transformer!");
    }
}

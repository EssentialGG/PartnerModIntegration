package com.example.mod.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class ExampleModClassTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.equals("net.minecraft.client.Minecraft")) {
            ClassNode classNode = new ClassNode();
            ClassReader reader = new ClassReader(basicClass);
            reader.accept(classNode, 0);

            for (MethodNode method : classNode.methods) {
                if ("<init>".equals(method.name)) {
                    method.instructions.insert(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/example/mod/ExampleMod", "transformerHello", "()V", false));
                }
            }

            ClassWriter writer = new ClassWriter(0);
            classNode.accept(writer);
            return writer.toByteArray();
        }
        return basicClass;
    }
}

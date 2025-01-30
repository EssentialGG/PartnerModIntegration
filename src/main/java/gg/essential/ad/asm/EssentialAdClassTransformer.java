package gg.essential.ad.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ListIterator;

public class EssentialAdClassTransformer implements IClassTransformer {
    private static final String PKG;
    static {
        String pkg = EssentialAdClassTransformer.class.getName();
        pkg = pkg.substring(0, pkg.length() - ".asm.EssentialAdClassTransformer".length());
        PKG = pkg.replace('.', '/');
    }
    private static final String ModalManager = PKG + "/modal/ModalManager";
    private static final String DrawEvent = PKG + "/modal/ModalManager$DrawEvent";

    // fixme: cleanup, port to prod
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.equals("net.minecraft.client.renderer.EntityRenderer")) {
            ClassNode classNode = new ClassNode();
            ClassReader reader = new ClassReader(basicClass);
            reader.accept(classNode, ClassReader.EXPAND_FRAMES);

            for (MethodNode method : classNode.methods) {
                String methodName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(classNode.name, method.name, method.desc);
                if (methodName.equals("updateCameraAndRender") || methodName.equals("func_181560_a")) {
                    ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
                    while (iterator.hasNext()) {
                        AbstractInsnNode abstractNode = iterator.next();
                        if (abstractNode instanceof MethodInsnNode) {
                            MethodInsnNode node = (MethodInsnNode) abstractNode;
                            // INVOKESTATIC net/minecraftforge/client/ForgeHooksClient.drawScreen (Lnet/minecraft/client/gui/GuiScreen;IIF)V
                            if (node.getOpcode() == Opcodes.INVOKESTATIC
                                && node.owner.equals("net/minecraftforge/client/ForgeHooksClient")
                                && node.name.equals("drawScreen")
                            ) {
                                iterator.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ModalManager, "drawScreenPriority", "()V", false));
                                break;
                            }
                        }
                    }
                }
            }

            ClassWriter writer = new ClassWriter(0);
            classNode.accept(writer);
            return writer.toByteArray();
        }
        if (transformedName.equals("net.minecraft.client.gui.GuiScreen")) {
            ClassNode classNode = new ClassNode();
            ClassReader reader = new ClassReader(basicClass);
            reader.accept(classNode, ClassReader.EXPAND_FRAMES);

            for (MethodNode method : classNode.methods) {
                String methodName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(classNode.name, method.name, method.desc);
                if (methodName.equals("drawScreen") || methodName.equals("func_73863_a")) {
                    InsnList list = new InsnList();
                    list.add(new TypeInsnNode(Opcodes.NEW, DrawEvent));
                    list.add(new InsnNode(Opcodes.DUP));
                    list.add(new VarInsnNode(Opcodes.ILOAD, 1));
                    list.add(new VarInsnNode(Opcodes.ILOAD, 2));
                    list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, DrawEvent, "<init>", "(II)V", false));
                    list.add(new VarInsnNode(Opcodes.ASTORE, 4));
                    list.add(new VarInsnNode(Opcodes.ALOAD, 4));
                    list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ModalManager, "preDraw", "(L" + DrawEvent + ";)V", false));
                    list.add(new VarInsnNode(Opcodes.ALOAD, 4));
                    list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, DrawEvent, "mouseXChanged", "()Z", false));
                    LabelNode skipXWrite = new LabelNode();
                    list.add(new JumpInsnNode(Opcodes.IFEQ, skipXWrite));
                    list.add(new VarInsnNode(Opcodes.ALOAD, 4));
                    list.add(new FieldInsnNode(Opcodes.GETFIELD, DrawEvent, "mouseX", "I"));
                    list.add(new VarInsnNode(Opcodes.ISTORE, 1));
                    list.add(skipXWrite);
                    list.add(new VarInsnNode(Opcodes.ALOAD, 4));
                    list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, DrawEvent, "mouseYChanged", "()Z", false));
                    LabelNode skipYWrite = new LabelNode();
                    list.add(new JumpInsnNode(Opcodes.IFEQ, skipYWrite));
                    list.add(new VarInsnNode(Opcodes.ALOAD, 4));
                    list.add(new FieldInsnNode(Opcodes.GETFIELD, DrawEvent, "mouseY", "I"));
                    list.add(new VarInsnNode(Opcodes.ISTORE, 2));
                    list.add(skipYWrite);
                    method.instructions.insertBefore(method.instructions.getFirst(), list);
                }
            }

            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            classNode.accept(writer);
//            byte[] bytes = writer.toByteArray();
//            try {
//                Files.write(Paths.get("out.class"), bytes);
//            } catch (Exception e) {
//                throw  new RuntimeException(e);
//            }
            return writer.toByteArray();
        }
        return basicClass;
    }
}

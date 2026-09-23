package dev.kors.gpucraft.tools;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Obfuscator {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage: Obfuscator <classes_dir>");
            System.exit(1);
        }

        File classesDir = new File(args[0]);
        if (!classesDir.exists() || !classesDir.isDirectory()) {
            System.err.println("Classes directory does not exist: " + classesDir);
            System.exit(1);
        }

        // Generate Decryptor class byte code: dev.kors.gpucraft.O0
        byte[] decryptorBytes = generateDecryptorClass();
        File decryptorFile = new File(classesDir, "dev/kors/gpucraft/O0.class");
        decryptorFile.getParentFile().mkdirs();
        Files.write(decryptorFile.toPath(), decryptorBytes);

        List<File> classFiles = new ArrayList<>();
        collectClassFiles(classesDir, classFiles);

        System.out.println("Obfuscating " + classFiles.size() + " classes...");

        for (File file : classFiles) {
            if (file.getName().equals("O0.class")) continue;
            obfuscateClass(file);
        }

        System.out.println("Obfuscation complete!");
    }

    private static void collectClassFiles(File dir, List<File> list) {
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File f : files) {
            if (f.isDirectory()) {
                collectClassFiles(f, list);
            } else if (f.getName().endsWith(".class")) {
                list.add(f);
            }
        }
    }

    private static void obfuscateClass(File file) throws Exception {
        byte[] original;
        try (FileInputStream fis = new FileInputStream(file)) {
            original = fis.readAllBytes();
        }

        ClassReader cr = new ClassReader(original);
        ClassNode cn = new ClassNode();
        cr.accept(cn, 0);

        // 1. Strip debug info
        cn.sourceFile = null;
        cn.sourceDebug = null;

        for (MethodNode mn : cn.methods) {
            mn.localVariables = null;
            mn.parameters = null;

            // Remove line numbers
            Iterator<AbstractInsnNode> it = mn.instructions.iterator();
            while (it.hasNext()) {
                AbstractInsnNode insn = it.next();
                if (insn instanceof LineNumberNode) {
                    it.remove();
                }
            }

            // 2. Encrypt Strings in method bytecode
            obfuscateStrings(mn);
        }

        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        byte[] modified = cw.toByteArray();

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(modified);
        }
    }

    private static void obfuscateStrings(MethodNode mn) {
        InsnList newInsns = new InsnList();
        for (AbstractInsnNode insn : mn.instructions.toArray()) {
            if (insn instanceof LdcInsnNode ldc && ldc.cst instanceof String str) {
                if (str.length() == 0) {
                    newInsns.add(insn);
                    continue;
                }
                int key = 0x5A3F ^ ((str.hashCode() & 0xFFFF) + 17);
                String encrypted = encrypt(str, key);

                InsnList patch = new InsnList();
                patch.add(new LdcInsnNode(encrypted));
                patch.add(new LdcInsnNode(key));
                patch.add(new MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        "dev/kors/gpucraft/O0",
                        "d",
                        "(Ljava/lang/String;I)Ljava/lang/String;",
                        false
                ));
                mn.instructions.insertBefore(insn, patch);
                mn.instructions.remove(insn);
            }
        }
    }


    public static String encrypt(String s, int k) {
        char[] c = s.toCharArray();
        for (int i = 0; i < c.length; i++) {
            c[i] = (char) (c[i] ^ (k + i * 31));
        }
        return new String(c);
    }

    private static byte[] generateDecryptorClass() {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        cw.visit(Opcodes.V21, Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL | Opcodes.ACC_SYNTHETIC,
                "dev/kors/gpucraft/O0", null, "java/lang/Object", null);

        // Constructor
        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PRIVATE, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        // public static String d(String s, int k)
        MethodVisitor dmv = cw.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                "d", "(Ljava/lang/String;I)Ljava/lang/String;", null, null);
        dmv.visitCode();

        dmv.visitVarInsn(Opcodes.ALOAD, 0);
        dmv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "toCharArray", "()[C", false);
        dmv.visitVarInsn(Opcodes.ASTORE, 2); // c

        dmv.visitInsn(Opcodes.ICONST_0);
        dmv.visitVarInsn(Opcodes.ISTORE, 3); // i = 0

        Label lLoop = new Label();
        Label lEnd = new Label();

        dmv.visitLabel(lLoop);
        dmv.visitVarInsn(Opcodes.ILOAD, 3);
        dmv.visitVarInsn(Opcodes.ALOAD, 2);
        dmv.visitInsn(Opcodes.ARRAYLENGTH);
        dmv.visitJumpInsn(Opcodes.IF_ICMPGE, lEnd);

        // c[i] = (char)(c[i] ^ (k + i * 31))
        dmv.visitVarInsn(Opcodes.ALOAD, 2);
        dmv.visitVarInsn(Opcodes.ILOAD, 3);

        dmv.visitVarInsn(Opcodes.ALOAD, 2);
        dmv.visitVarInsn(Opcodes.ILOAD, 3);
        dmv.visitInsn(Opcodes.CALOAD);

        dmv.visitVarInsn(Opcodes.ILOAD, 1);
        dmv.visitVarInsn(Opcodes.ILOAD, 3);
        dmv.visitIntInsn(Opcodes.BIPUSH, 31);
        dmv.visitInsn(Opcodes.IMUL);
        dmv.visitInsn(Opcodes.IADD);

        dmv.visitInsn(Opcodes.IXOR);
        dmv.visitInsn(Opcodes.I2C);
        dmv.visitInsn(Opcodes.CASTORE);

        // i++
        dmv.visitIincInsn(3, 1);
        dmv.visitJumpInsn(Opcodes.GOTO, lLoop);

        dmv.visitLabel(lEnd);
        dmv.visitTypeInsn(Opcodes.NEW, "java/lang/String");
        dmv.visitInsn(Opcodes.DUP);
        dmv.visitVarInsn(Opcodes.ALOAD, 2);
        dmv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/String", "<init>", "([C)V", false);
        dmv.visitInsn(Opcodes.ARETURN);

        dmv.visitMaxs(0, 0);
        dmv.visitEnd();

        cw.visitEnd();
        return cw.toByteArray();
    }
}

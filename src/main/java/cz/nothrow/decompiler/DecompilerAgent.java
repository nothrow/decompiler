package cz.nothrow.decompiler;

import org.jboss.windup.decompiler.api.ClassDecompileRequest;
import org.jboss.windup.decompiler.fernflower.FernflowerDecompiler;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;

public class DecompilerAgent {

    private static String filter;

    public static void premain(String agentArgs, Instrumentation inst) {
        filter = agentArgs;

        System.out.println("Bytecode Dumper Agent is active, filter for classes is: " + filter);
        try {

            Files.createDirectories(Paths.get("dumped_bytecode/"));
            Files.createDirectories(Paths.get("dumped_java/"));

            inst.addTransformer(new Transformer());
        } catch (IOException e) {
            System.out.println("Failed to create directory");
        }
    }

    static class Transformer implements ClassFileTransformer {
        FernflowerDecompiler decompiler;

        public Transformer() {
            decompiler = new FernflowerDecompiler();
        }

        public void decompile(Path bytecode) {
            var target = Paths.get("dumped_java");
            decompiler.decompileClassFile(Paths.get("dumped_bytecode"), bytecode, target);
        }

        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                ProtectionDomain protectionDomain, byte[] classfileBuffer) {
            if (className != null && className.replace("/", ".").startsWith(filter)) {  // Specify the package filter here
                var bytecodeFilename = Paths.get("dumped_bytecode", className.replace("/", ".") + ".class");

                try (FileOutputStream fos = new FileOutputStream(bytecodeFilename.toString())) {
                    fos.write(classfileBuffer);
                } catch (Exception e) {
                    System.err.println("Failed to dump bytecode for " + className);
                    e.printStackTrace();
                    return classfileBuffer;
                }

                try {
                    decompile(bytecodeFilename);
                } catch (Exception e) {
                    System.err.println("Failed to decompile bytecode for " + className);
                    e.printStackTrace();
                }
            }

            return classfileBuffer;  // Return the unmodified class bytes
        }
    }
}

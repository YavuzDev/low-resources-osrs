import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.reflections.Reflections;
import reader.NameAndInputStream;
import reader.ObfuscatedClass;
import visitor.HookVisitor;
import visitor.condition.Condition;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

public class HooksFinder {

    private static final boolean DOWNLOAD_JAR = false;

    private static final boolean UNZIP_JAR = false;

    public static void main(String[] args) throws Exception {
        var jarUrl = "http://oldschool83.runescape.com/gamepack_for_kaleem_and_emre_bot_client.jar";
        var jarFile = Path.of("resources", "hooks", "gamepack.jar");
        if (DOWNLOAD_JAR) {
            FileUtils.copyURLToFile(new URL(jarUrl), jarFile.toFile());
        }

        var inputStreams = unZipJar(jarFile);
        var classes = read(inputStreams);

        loadVisitors(jarFile, classes);
    }

    private static InputStream getInputStreamForClass(Path jarPath, String fileName) throws IOException {
        var unzippedDirectory = jarPath.getParent().resolve("unzipped");
        return Files.newInputStream(unzippedDirectory.resolve(fileName + ".class"));
    }

    private static void loadVisitors(Path jarPath, List<ObfuscatedClass> obfuscatedClasses) {
        var reflections = new Reflections("visitor.impl");
        var classes = reflections.getSubTypesOf(HookVisitor.class);

        classes.forEach(c -> {
            try {
                var instance = c.getDeclaredConstructor().newInstance();
                var correctClass = getCorrectClassFromConditions(instance.conditions(), obfuscatedClasses);
                if (correctClass == null) {
                    throw new FileNotFoundException("Unable to find class for " + instance + " with conditions: " + instance.conditions());
                }
                instance.setCurrentClass(correctClass);

                var inputStream = getInputStreamForClass(jarPath, correctClass.getName());
                var classReader = new ClassReader(inputStream);
                classReader.accept(instance, ClassReader.EXPAND_FRAMES);

                inputStream.close();

                var gson = new GsonBuilder().setPrettyPrinting().create();
                Files.write(jarPath.getParent().resolve("hooks.json"), gson.toJson(instance.getHooks()).getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static ObfuscatedClass getCorrectClassFromConditions(List<Condition> conditions, List<ObfuscatedClass> obfuscatedClasses) {
        var correctConditionsCount = 0;
        for (var obfuscatedClass : obfuscatedClasses) {
            for (var condition : conditions) {
                if (condition.check(obfuscatedClass)) {
                    correctConditionsCount++;
                }
            }
            if (correctConditionsCount < conditions.size()) {
                correctConditionsCount = 0;
                continue;
            }
            return obfuscatedClass;
        }
        return null;
    }

    private static List<NameAndInputStream> unZipJar(Path jarPath) throws IOException {
        var streams = new ArrayList<NameAndInputStream>();
        var jarFile = new JarFile(jarPath.toString());

        var unzipDirectory = jarPath.getParent().resolve("unzipped");
        if (!Files.exists(unzipDirectory)) {
            Files.createDirectory(unzipDirectory);
        }
        jarFile.versionedStream()
                .filter(jarEntry -> jarEntry.getName().endsWith(".class"))
                .forEach(jarEntry -> {
                    try {
                        var inputStream = jarFile.getInputStream(jarEntry);
                        streams.add(new NameAndInputStream(jarEntry.getName(), jarFile.getInputStream(jarEntry)));
                        if (UNZIP_JAR) {
                            FileUtils.copyInputStreamToFile(inputStream, unzipDirectory.resolve(jarEntry.getName()).toFile());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        return streams;
    }

    private static List<ObfuscatedClass> read(List<NameAndInputStream> inputStreams) {
        var list = new ArrayList<ObfuscatedClass>();

        inputStreams.forEach(nameAndInputStream -> {
            try {
                var classReader = new ClassReader(nameAndInputStream.getInputStream());

                var classNode = new ClassNode();
                classReader.accept(classNode, ClassReader.EXPAND_FRAMES);

                var obfuscatedClass = new ObfuscatedClass(nameAndInputStream.getFileName().replace(".class", ""), classNode);

                list.add(obfuscatedClass);

                nameAndInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return list;
    }
}

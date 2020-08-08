import com.google.gson.GsonBuilder;
import hook.Hooks;
import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.reflections.Reflections;
import reader.NameAndInputStream;
import reader.ObfuscatedClass;
import visitor.DependsOn;
import visitor.HookVisitor;
import visitor.condition.Condition;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.logging.Logger;

public class HooksFinder {

    private static final Logger LOGGER = Logger.getLogger(HooksFinder.class.getName());

    private static final boolean DOWNLOAD_JAR = false;

    private static final boolean UNZIP_JAR = false;

    public static void main(String[] args) throws Exception {
        var jarUrl = "http://oldschool83.runescape.com/gamepack_for_kaleem_and_emre_bot_client.jar";
        var jarFile = Path.of("resources", "hooks", "gamepack.jar");
        if (DOWNLOAD_JAR) {
            LOGGER.info("Downloading jar");
            FileUtils.copyURLToFile(new URL(jarUrl), jarFile.toFile());
        }

        if (!Files.exists(jarFile)) {
            throw new FileNotFoundException("Jar not found set DOWNLOAD_JAR to true");
        }

        var inputStreams = unZipJar(jarFile);
        var classes = read(inputStreams);

        loadVisitors(jarFile, classes);
    }

    private static InputStream getInputStreamForClass(Path jarPath, String fileName) throws IOException {
        var unzippedDirectory = jarPath.getParent().resolve("unzipped");
        var filePath = unzippedDirectory.resolve(fileName + ".class");
        if (!Files.exists(unzippedDirectory) || !Files.exists(filePath)) {
            throw new FileNotFoundException("Unzipped files not found set UNZIP_JAR to true");
        }
        return Files.newInputStream(filePath);
    }

    private static void loadVisitors(Path jarPath, List<ObfuscatedClass> obfuscatedClasses) throws IOException {
        var reflections = new Reflections("visitor.impl");
        var classes = reflections.getSubTypesOf(HookVisitor.class);

        var hooks = new Hooks();

        var visitors = new ArrayList<DependentVisitor>();
        classes.forEach(c -> {
            try {
                var instance = c.getDeclaredConstructor(Hooks.class, List.class).newInstance(hooks, obfuscatedClasses);
                var dependsOn = c.getAnnotation(DependsOn.class);
                if (dependsOn != null && dependsOn.value().length > 0) {
                    visitors.add(new DependentVisitor(instance, dependsOn.value()));
                    return;
                }
                findHooks(instance, jarPath, obfuscatedClasses);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        var visited = new HashSet<DependentVisitor>();
        visitors.forEach(v -> {
            try {
                visit(v, jarPath, obfuscatedClasses, visitors, visited);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        var gson = new GsonBuilder().setPrettyPrinting().create();
        Files.write(jarPath.getParent().resolve("hooks.json"), gson.toJson(hooks).getBytes());
    }

    private static void visit(DependentVisitor dependentVisitor, Path jarPath, List<ObfuscatedClass> obfuscatedClasses, List<DependentVisitor> visitors, Set<DependentVisitor> visited) throws IOException {
        if (!visited.contains(dependentVisitor)) {
            for (var dependents : dependentVisitor.getDependents()) {
                for (var visitor : visitors) {
                    if (visitor.getHookVisitor().getClass().getName().equals(dependents.getName())) {
                        visit(visitor, jarPath, obfuscatedClasses, visitors, visited);
                    }
                }
            }
        }
        visited.add(dependentVisitor);
        findHooks(dependentVisitor.getHookVisitor(), jarPath, obfuscatedClasses);
    }

    private static void findHooks(HookVisitor instance, Path jarPath, List<ObfuscatedClass> obfuscatedClasses) throws IOException {
        LOGGER.info("Visiting " + instance.getClass().getName());
        var correctClass = getCorrectClassFromConditions(instance.conditions(), obfuscatedClasses);
        if (correctClass == null) {
            throw new FileNotFoundException("Unable to find class for " + instance + " with conditions: " + instance.conditions());
        }
        instance.setCurrentClass(correctClass);

        var inputStream = getInputStreamForClass(jarPath, correctClass.getName());

        var classReader = new ClassReader(inputStream);
        classReader.accept(instance, ClassReader.EXPAND_FRAMES);

        inputStream.close();
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
            LOGGER.info("Found " + obfuscatedClass + " from conditions " + conditions);
            return obfuscatedClass;
        }
        throw new NullPointerException("No class found with conditions: " + conditions);
    }

    private static List<NameAndInputStream> unZipJar(Path jarPath) throws IOException {
        LOGGER.info("Reading input streams from jar " + jarPath);

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

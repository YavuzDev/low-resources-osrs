package com.bot;

import com.bot.hook.Hooks;
import com.bot.reader.NameAndInputStream;
import com.bot.reader.ObfuscatedClass;
import com.bot.visitor.HookVisitor;
import com.bot.visitor.VisitorInfo;
import com.bot.visitor.condition.Condition;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public class HooksFinder {

    private static final Logger LOGGER = LoggerFactory.getLogger(HooksFinder.class);

    private static final String JAR_URL = "http://oldschool83.runescape.com/gamepack_for_kaleem_and_emre_bot_client.jar";

    private static final String HOOKS_PACKAGE = "com.bot.visitor.impl";

    public static final Path RESOURCES_DIRECTORY = Path.of("resources");

    public static final Path HOOKS_PATH_DIRECTORY = RESOURCES_DIRECTORY.resolve("hooks");

    public static final Path HOOKS_JSON_PATH = HOOKS_PATH_DIRECTORY.resolve("hooks.json");

    public static final Path JAR_FILE = HOOKS_PATH_DIRECTORY.resolve("gamepack.jar");

    private static final boolean DOWNLOAD_JAR = false;

    private static final boolean UNZIP_JAR = false;

    public static void main(String[] args) throws Exception {
        if (DOWNLOAD_JAR) {
            LOGGER.info("Downloading jar from {}", JAR_URL);
            FileUtils.copyURLToFile(new URL(JAR_URL), JAR_FILE.toFile());
        }

        if (!Files.exists(JAR_FILE)) {
            throw new FileNotFoundException(JAR_FILE + " not found set DOWNLOAD_JAR to true");
        }

        var inputStreams = unZipJar();
        var classes = read(inputStreams);

        loadVisitors(classes);
    }

    private static InputStream getInputStreamForClass(Path jarPath, String fileName) throws IOException {
        var unzippedDirectory = jarPath.getParent().resolve("unzipped");
        var filePath = unzippedDirectory.resolve(fileName + ".class");
        if (!Files.exists(unzippedDirectory) || !Files.exists(filePath)) {
            throw new FileNotFoundException("Unzipped files at " + unzippedDirectory + " not found set UNZIP_JAR to true");
        }
        return Files.newInputStream(filePath);
    }

    private static void loadVisitors(List<ObfuscatedClass> obfuscatedClasses) throws IOException {
        LOGGER.info("Looking through package {} to load visitors", HOOKS_PACKAGE);
        var reflections = new Reflections(HOOKS_PACKAGE);
        var classes = reflections.getSubTypesOf(HookVisitor.class);

        var hooks = new Hooks();

        var visitors = new ArrayList<DependentVisitor>();
        classes.forEach(c -> {
            try {
                var instance = c.getDeclaredConstructor().newInstance();
                instance.setAllClasses(obfuscatedClasses);
                instance.setHooks(hooks);

                var info = c.getAnnotation(VisitorInfo.class);
                if (info == null) {
                    throw new NullPointerException(instance.getClass().getName() + " doesn't have the VisitorInfo annotation");
                }
                if (info.name().isBlank()) {
                    throw new NullPointerException(instance.getClass().getName() + " Name in VisitorInfo is blank");
                }
                if (info.dependsOn().length > 0) {
                    visitors.add(new DependentVisitor(instance, info.dependsOn()));
                    return;
                }
                findHooks(instance, HooksFinder.JAR_FILE, obfuscatedClasses);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        });

        var visited = new HashSet<DependentVisitor>();
        visitors.forEach(v -> {
            try {
                visit(v, HooksFinder.JAR_FILE, obfuscatedClasses, visitors, visited);
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        });

        var gson = new GsonBuilder().setPrettyPrinting().create();
        Files.write(HOOKS_JSON_PATH, gson.toJson(hooks).getBytes());
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
        LOGGER.info("Visiting {}", instance.getClass().getName());

        var info = instance.getClass().getAnnotation(VisitorInfo.class);
        var correctClass = getCorrectClassFromConditions(instance.conditions(), obfuscatedClasses);
        instance.setCurrentClass(correctClass, info.name());

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
            LOGGER.info("Found class {} from conditions {}", obfuscatedClass.getName(), conditions);
            return obfuscatedClass;
        }
        throw new NullPointerException("No class found with conditions: " + conditions);
    }

    private static List<NameAndInputStream> unZipJar() throws IOException {
        LOGGER.info("Reading input streams from jar {}", HooksFinder.JAR_FILE);

        var streams = new ArrayList<NameAndInputStream>();
        var jarFile = new JarFile(HooksFinder.JAR_FILE.toString());

        var unzipDirectory = HooksFinder.JAR_FILE.getParent().resolve("unzipped");
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
                        LOGGER.error(e.getMessage(), e);
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

//                obfuscatedClass.getClassNode().methods.forEach(m -> m.instructions.forEach(i -> {
//                    if (i.getOpcode() == Opcodes.IF_ICMPNE) {
//                        var jumpsInNode = (JumpInsnNode) i;
//                        if (jumpsInNode.getPrevious() instanceof IntInsnNode) {
//                            var intIns = (IntInsnNode) jumpsInNode.getPrevious();
//                            if (intIns.operand == 1504 || intIns.operand == 2504) {
//                                System.out.println("Found in class jump previous: " + obfuscatedClass.getName() + " function: " + m.name);
//                            }
//                        }
//                        if (jumpsInNode.getPrevious() instanceof VarInsnNode) {
//                            var variable = (VarInsnNode) jumpsInNode.getPrevious();
//                            if (variable.getPrevious() instanceof LdcInsnNode) {
//                                var ldc = (LdcInsnNode) variable.getPrevious();
//                                if (ldc.cst instanceof Integer) {
//                                    var value = (int) ldc.cst;
//                                    if (value == 1504 || value == 2504) {
//                                        System.out.println("Found in class ldc: " + obfuscatedClass.getName() + " function: " + m.name);
//                                    }
//                                }
//                            }
//                            if (variable.getPrevious() instanceof IntInsnNode) {
//                                var intIns = (IntInsnNode) variable.getPrevious();
//                                if (intIns.operand == 1504 || intIns.operand == 2504) {
//                                    System.out.println("Found in class var prev: " + obfuscatedClass.getName() + " function: " + m.name);
//                                }
//                            }
//                        }
//                    }
//                }));

                nameAndInputStream.close();
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        });
        return list;
    }
}

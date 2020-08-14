package com.osrs.hook;

import com.osrs.hook.global.StaticFieldHook;
import com.osrs.hook.global.StaticHooks;
import com.osrs.hook.global.StaticMethodHook;
import com.osrs.hook.local.ClassHook;
import com.osrs.reader.ObfuscatedClass;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Hooks {

    private final StaticHooks statics;

    private final Map<String, ClassHook> classes;

    public Hooks(StaticHooks statics, Map<String, ClassHook> classes) {
        this.statics = statics;
        this.classes = classes;
    }

    public Hooks() {
        this.statics = new StaticHooks();
        this.classes = new HashMap<>();
    }

    public ClassHook getClassHook(String className) {
        return classes.get(className);
    }

    public Map<String, ClassHook> getClasses() {
        return classes;
    }

    public StaticHooks getStatics() {
        return statics;
    }

    public boolean containsStaticField(String givenName) {
        return statics.getFields().containsKey(givenName);
    }

    public boolean containsClass(ObfuscatedClass obfuscatedClass) {
        return classes
                .entrySet()
                .stream()
                .anyMatch(c -> c.getValue().getName().equals(obfuscatedClass.getName()));
    }

    public void addClassHook(String givenName, ClassHook classHook) {
        this.classes.put(givenName, classHook);
    }

    public ClassHook getCorrectClass(ObfuscatedClass currentClass) {
        return Objects.requireNonNull(classes
                .entrySet()
                .stream()
                .filter(c -> c.getValue().getName().equals(currentClass.getName()))
                .findFirst()
                .orElse(null))
                .getValue();
    }

    public StaticMethodHook getStaticMethod(String value) {
        return statics.getMethods().get(value);
    }

    public StaticFieldHook getStaticField(String fieldName) {
        return statics.getFields().get(fieldName);
    }

    @Override
    public String toString() {
        return "Hooks{" +
                "statics=" + statics +
                ", classes=" + classes +
                '}';
    }

}

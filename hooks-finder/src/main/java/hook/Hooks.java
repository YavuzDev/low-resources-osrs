package hook;

import hook.global.StaticHooks;
import hook.local.ClassHook;
import reader.ObfuscatedClass;

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

    public Map<String, ClassHook> getClasses() {
        return classes;
    }

    public StaticHooks getStatics() {
        return statics;
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

    @Override
    public String toString() {
        return "Hooks{" +
                "statics=" + statics +
                ", classes=" + classes +
                '}';
    }
}

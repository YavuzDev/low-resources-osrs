package hook.local;

import java.util.HashMap;
import java.util.Map;

public class ClassHook {

    private final String name;

    private final Map<String, FieldHook> fields;

    private final Map<String, MethodHook> methods;

    public ClassHook(String name, Map<String, FieldHook> fields, Map<String, MethodHook> methods) {
        this.name = name;
        this.fields = fields;
        this.methods = methods;
    }

    public ClassHook(String name) {
        this.name = name;
        this.fields = new HashMap<>();
        this.methods = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public Map<String, FieldHook> getFields() {
        return fields;
    }

    public Map<String, MethodHook> getMethods() {
        return methods;
    }

    public void addFieldHook(String givenName, FieldHook fieldHook) {
        this.fields.put(givenName, fieldHook);
    }

    public void addMethodHook(String givenName, MethodHook methodHook) {
        this.methods.put(givenName, methodHook);
    }

    @Override
    public String toString() {
        return "ClassHook{" +
                "name='" + name + '\'' +
                ", fields=" + fields +
                ", methods=" + methods +
                '}';
    }
}

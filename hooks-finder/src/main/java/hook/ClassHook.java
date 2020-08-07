package hook;

import java.util.HashMap;
import java.util.Map;

public class ClassHook {

    private final String name;

    private final Map<String, FieldHook> fields;

    public ClassHook(String name) {
        this.name = name;
        this.fields = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public Map<String, FieldHook> getFields() {
        return fields;
    }

    public void addFieldHook(String givenName, FieldHook fieldHook) {
        this.fields.put(givenName, fieldHook);
    }

    @Override
    public String toString() {
        return "ClassHook{" +
                "name='" + name + '\'' +
                ", fields=" + fields +
                '}';
    }
}

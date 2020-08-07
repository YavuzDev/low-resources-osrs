package hook.local;

public class FieldHook {

    private final String name;

    private final String type;

    public FieldHook(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "FieldHook{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}

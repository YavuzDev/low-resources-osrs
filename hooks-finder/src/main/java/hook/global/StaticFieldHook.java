package hook.global;

public class StaticFieldHook {

    private final String owner;

    private final String name;

    private final String type;

    public StaticFieldHook(String owner, String name, String type) {
        this.owner = owner;
        this.name = name;
        this.type = type;
    }

    public String getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "StaticFieldHook{" +
                "owner='" + owner + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}

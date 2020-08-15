package com.osrs.hook.local;

public class MethodHook {

    private final String name;

    private final String type;

    private final Integer dummyValue;

    public MethodHook(String name, String type) {
        this(name, type, null);
    }

    public MethodHook(String name, String type, Integer dummyValue) {
        this.name = name;
        this.type = type;
        this.dummyValue = dummyValue;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Integer getDummyValue() {
        return dummyValue;
    }

    @Override
    public String toString() {
        return "MethodHook{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", dummyValue=" + dummyValue +
                '}';
    }
}

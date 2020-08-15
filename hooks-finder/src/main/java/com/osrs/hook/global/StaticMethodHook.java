package com.osrs.hook.global;

public class StaticMethodHook {

    private final String owner;

    private final String name;

    private final String type;

    private final Integer dummyValue;

    public StaticMethodHook(String owner, String name, String type) {
        this(owner, name, type, null);
    }

    public StaticMethodHook(String owner, String name, String type, Integer dummyValue) {
        this.owner = owner;
        this.name = name;
        this.type = type;
        this.dummyValue = dummyValue;
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

    public Integer getDummyValue() {
        return dummyValue;
    }

    @Override
    public String toString() {
        return "StaticMethodHook{" +
                "owner='" + owner + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", dummyValue=" + dummyValue +
                '}';
    }
}

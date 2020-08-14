package com.osrs.hook.global;

public class StaticMethodHook {

    private final String owner;

    private final String name;

    private final String type;

    public StaticMethodHook(String owner, String name, String type) {
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
        return "StaticMethodHook{" +
                "owner='" + owner + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}

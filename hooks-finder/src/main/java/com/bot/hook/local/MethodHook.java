package com.bot.hook.local;

public class MethodHook {

    private final String name;

    private final String type;

    public MethodHook(String name, String type) {
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
        return "MethodHook{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}

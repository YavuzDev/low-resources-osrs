package com.bot.hook.global;

import java.util.HashMap;
import java.util.Map;

public class StaticHooks {

    private final Map<String, StaticFieldHook> fields;

    private final Map<String, StaticMethodHook> methods;

    public StaticHooks(Map<String, StaticFieldHook> fields, Map<String, StaticMethodHook> methods) {
        this.fields = fields;
        this.methods = methods;
    }

    public StaticHooks() {
        this.fields = new HashMap<>();
        this.methods = new HashMap<>();
    }

    public Map<String, StaticFieldHook> getFields() {
        return fields;
    }

    public Map<String, StaticMethodHook> getMethods() {
        return methods;
    }

    public void addField(String givenName, StaticFieldHook staticFieldHook) {
        this.fields.put(givenName, staticFieldHook);
    }

    public void addMethod(String givenName, StaticMethodHook staticMethodHook) {
        this.methods.put(givenName, staticMethodHook);
    }

    @Override
    public String toString() {
        return "StaticHooks{" +
                "fields=" + fields +
                ", methods=" + methods +
                '}';
    }
}

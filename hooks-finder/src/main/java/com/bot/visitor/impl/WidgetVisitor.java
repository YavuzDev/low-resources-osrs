package com.bot.visitor.impl;

import com.bot.hook.Hooks;
import com.bot.reader.ObfuscatedClass;
import com.bot.visitor.HookVisitor;
import com.bot.visitor.condition.Condition;

import java.util.List;

public class WidgetVisitor extends HookVisitor {

    public WidgetVisitor(Hooks hooks, List<ObfuscatedClass> allClasses) {
        super(hooks, allClasses);
    }

    @Override
    public List<Condition> conditions() {
        return List.of(fieldCondition(50, 80, "Int"), fieldCondition(6, "String"));
    }

    @Override
    public String className() {
        return "Widget";
    }

    @Override
    public void onSetClassNode() {

    }
}

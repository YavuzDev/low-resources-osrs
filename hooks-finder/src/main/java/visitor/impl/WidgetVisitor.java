package visitor.impl;

import hook.Hooks;
import reader.ObfuscatedClass;
import visitor.HookVisitor;
import visitor.condition.Condition;

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

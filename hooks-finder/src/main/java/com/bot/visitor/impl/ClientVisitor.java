package com.bot.visitor.impl;

import com.bot.hook.Hooks;
import com.bot.hook.global.StaticFieldHook;
import org.objectweb.asm.Opcodes;
import com.bot.reader.ObfuscatedClass;
import com.bot.visitor.DependsOn;
import com.bot.visitor.HookVisitor;
import com.bot.visitor.condition.Condition;

import java.util.List;

@DependsOn({WidgetVisitor.class})
public class ClientVisitor extends HookVisitor {

    public ClientVisitor(Hooks hooks, List<ObfuscatedClass> allClasses) {
        super(hooks, allClasses);
    }

    @Override
    public List<Condition> conditions() {
        return List.of(fieldCondition(5, "Widget"));
    }

    @Override
    public String className() {
        return "Client";
    }

    @Override
    public void onSetClassNode() {
        var method = getMethod(parameterCondition(1, "Widget"), parameterCondition(2, "Int"));
        var viewport = getField(method, opcodeCondition(Opcodes.PUTSTATIC), fieldCondition(1, "Widget"));

        addStaticFieldHook("viewport", new StaticFieldHook(getOwner(), viewport.name, viewport.desc));
    }
}

package com.bot.visitor.impl;

import com.bot.hook.Hooks;
import com.bot.hook.global.StaticFieldHook;
import com.bot.reader.ObfuscatedClass;
import com.bot.visitor.HookVisitor;
import com.bot.visitor.VisitorInfo;
import com.bot.visitor.condition.Condition;
import org.objectweb.asm.Opcodes;

import java.util.List;

@VisitorInfo(name = "Client", dependsOn = {WidgetVisitor.class})
public class ClientVisitor extends HookVisitor {

    public ClientVisitor(Hooks hooks, List<ObfuscatedClass> allClasses) {
        super(hooks, allClasses);
    }

    @Override
    public List<Condition> conditions() {
        return List.of(fieldCondition(5, "Widget"));
    }

    @Override
    public void onSetClassNode() {
        var method = getMethod(parameterCondition("Widget"), parameterCondition(2, "Int"));
        var viewport = getField(method, opcodeCondition(Opcodes.PUTSTATIC), fieldCondition("Widget"));

        addStaticFieldHook("viewport", new StaticFieldHook(getOwner(), viewport.name, viewport.desc));
    }
}

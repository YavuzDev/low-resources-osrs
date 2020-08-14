package com.osrs.visitor.impl;

import com.osrs.hook.global.StaticFieldHook;
import com.osrs.visitor.HookVisitor;
import com.osrs.visitor.VisitorInfo;
import com.osrs.visitor.condition.Condition;
import org.objectweb.asm.Opcodes;

import java.util.List;

@VisitorInfo(name = "Client", dependsOn = {WidgetVisitor.class})
public class ClientVisitor extends HookVisitor {

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

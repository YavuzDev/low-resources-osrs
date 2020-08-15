package com.osrs.visitor.impl;

import com.osrs.hook.global.StaticMethodHook;
import com.osrs.visitor.HookVisitor;
import com.osrs.visitor.VisitorInfo;
import com.osrs.visitor.condition.Condition;

import java.util.List;

@VisitorInfo(name = "LongVarSerializer", dependsOn = {PacketBufferVisitor.class})
public class LongVarSerializerVisitor extends HookVisitor {

    @Override
    public List<Condition> conditions() {
        return List.of(fieldCondition(4, "Integer"), staticFieldCondition(4, 24, "Integer"));
    }

    @Override
    public void onSetClassNode() {
        var updateNpcs = getMethod(parameterCondition("Boolean"), parameterCondition("PacketBuffer"));
        addStaticMethodHook("updateNpcs", new StaticMethodHook(getOwner(), updateNpcs.name, updateNpcs.desc, updateNpcs.parameters.size() > 2 ? 1542604247 : null));
    }
}

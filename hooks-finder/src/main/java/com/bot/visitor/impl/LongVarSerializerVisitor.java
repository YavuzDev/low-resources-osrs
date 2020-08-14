package com.bot.visitor.impl;

import com.bot.hook.global.StaticMethodHook;
import com.bot.visitor.HookVisitor;
import com.bot.visitor.VisitorInfo;
import com.bot.visitor.condition.Condition;

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
        addStaticMethodHook("updateNpcs", new StaticMethodHook(getOwner(), updateNpcs.name, updateNpcs.desc));
    }
}

package com.bot.visitor.impl;

import com.bot.visitor.HookVisitor;
import com.bot.visitor.VisitorInfo;
import com.bot.visitor.condition.Condition;

import java.util.List;

@VisitorInfo(name = "PacketBuffer")
public class PacketBufferVisitor extends HookVisitor {

    @Override
    public List<Condition> conditions() {
        return List.of(fieldCondition(3, "Integer"), staticFieldCondition(24, "IntegerArray"), staticFieldCondition(2, 24, "Integer"));
    }

    @Override
    public void onSetClassNode() {
    }
}

package com.osrs.visitor.impl;

import com.osrs.visitor.HookVisitor;
import com.osrs.visitor.VisitorInfo;
import com.osrs.visitor.condition.Condition;

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

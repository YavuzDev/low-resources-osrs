package com.osrs.visitor.impl;

import com.osrs.visitor.HookVisitor;
import com.osrs.visitor.VisitorInfo;
import com.osrs.visitor.condition.Condition;

import java.util.List;

@VisitorInfo(name = "AE", dependsOn = {ClientVisitor.class})
public class AEVisitor extends HookVisitor {

    @Override
    public List<Condition> conditions() {
        return List.of(fieldCondition("Client"));
    }

    @Override
    public void onSetClassNode() {
        var fields = getCurrentClass().getFields();
        var client = getFieldFromType(fields, "Client");

        addStaticFieldHook("client", client);
    }
}

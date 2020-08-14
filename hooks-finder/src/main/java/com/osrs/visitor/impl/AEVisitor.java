package com.osrs.visitor.impl;

import com.osrs.hook.global.StaticFieldHook;
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
        fields.stream()
                .filter(f -> f.desc.equals(correctType("Client")))
                .findFirst()
                .ifPresent(client -> addStaticFieldHook("client", new StaticFieldHook(getOwner(), client.name, client.desc)));

    }
}

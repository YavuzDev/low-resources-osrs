package com.bot.visitor.impl;

import com.bot.hook.global.StaticFieldHook;
import com.bot.visitor.HookVisitor;
import com.bot.visitor.VisitorInfo;
import com.bot.visitor.condition.Condition;

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

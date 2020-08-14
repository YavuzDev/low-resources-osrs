package com.osrs.visitor.impl;

import com.osrs.hook.local.MethodHook;
import com.osrs.visitor.HookVisitor;
import com.osrs.visitor.VisitorInfo;
import com.osrs.visitor.condition.Condition;

import java.util.List;

@VisitorInfo(name = "GameEngine")
public class GameEngineVisitor extends HookVisitor {

    @Override
    public List<Condition> conditions() {
        return List.of(fieldCondition(12, 15, "Int"), fieldCondition(4, 6, "Boolean"), fieldCondition(2, 4, "Long"));
    }

    @Override
    public void onSetClassNode() {
        var post = getMethod(parameterCondition("Byte"));
        addMethodHook("post", new MethodHook(post.name, post.desc));
    }
}

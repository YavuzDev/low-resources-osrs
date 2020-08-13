package com.bot.visitor.impl;

import com.bot.hook.local.MethodHook;
import com.bot.visitor.HookVisitor;
import com.bot.visitor.VisitorInfo;
import com.bot.visitor.condition.Condition;

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

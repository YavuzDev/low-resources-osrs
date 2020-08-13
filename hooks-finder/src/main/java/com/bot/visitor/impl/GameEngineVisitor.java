package com.bot.visitor.impl;

import com.bot.hook.Hooks;
import com.bot.reader.ObfuscatedClass;
import com.bot.visitor.HookVisitor;
import com.bot.visitor.VisitorInfo;
import com.bot.visitor.condition.Condition;

import java.util.List;

@VisitorInfo(name = "GameEngine")
public class GameEngineVisitor extends HookVisitor {

    public GameEngineVisitor(Hooks hooks, List<ObfuscatedClass> allClasses) {
        super(hooks, allClasses);
    }

    @Override
    public List<Condition> conditions() {
        return List.of(fieldCondition(12, 15, "Int"), fieldCondition(4, 6, "Boolean"), fieldCondition(2, 4, "Long"));
    }

    @Override
    public void onSetClassNode() {

    }
}

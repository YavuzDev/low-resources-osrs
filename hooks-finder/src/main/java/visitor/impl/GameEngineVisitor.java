package visitor.impl;

import visitor.HookVisitor;
import visitor.condition.Condition;

import java.util.List;

public class GameEngineVisitor extends HookVisitor {

    @Override
    public List<Condition> conditions() {
        return List.of(fieldCondition(1, "I"));
    }

    @Override
    public String className() {
        return "GameEngine";
    }
}

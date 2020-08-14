package com.osrs.visitor.impl;

import com.osrs.hook.local.FieldHook;
import com.osrs.visitor.HookVisitor;
import com.osrs.visitor.VisitorInfo;
import com.osrs.visitor.condition.Condition;

import java.util.List;

@VisitorInfo(name = "Widget")
public class WidgetVisitor extends HookVisitor {

    @Override
    public List<Condition> conditions() {
        return List.of(fieldCondition(50, 80, "Int"), fieldCondition(6, "String"));
    }

    @Override
    public void onSetClassNode() {
        var hidden = getFieldsFromCount(callCountCondition(7, "Boolean"));
        for (int i = 0; i < hidden.size(); i++) {
            addFieldHook("hidden" + i, new FieldHook(hidden.get(i).getName(), hidden.get(i).getType()));
        }
    }
}

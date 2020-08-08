package visitor.impl;

import hook.Hooks;
import hook.global.StaticFieldHook;
import org.objectweb.asm.Opcodes;
import reader.ObfuscatedClass;
import visitor.DependsOn;
import visitor.HookVisitor;
import visitor.condition.Condition;

import java.util.List;

@DependsOn({WidgetVisitor.class})
public class ClientVisitor extends HookVisitor {

    public ClientVisitor(Hooks hooks, List<ObfuscatedClass> allClasses) {
        super(hooks, allClasses);
    }

    @Override
    public List<Condition> conditions() {
        return List.of(fieldCondition(5, "Widget"));
    }

    @Override
    public String className() {
        return "Client";
    }

    @Override
    public void onSetClassNode() {
        var method = getMethod(parameterCondition(1, "Widget"), parameterCondition(2, "Int"));
        var viewport = getField(method, opcodeCondition(Opcodes.PUTSTATIC), fieldCondition(1, "Widget"));

        addStaticFieldHook("viewport", new StaticFieldHook(getOwner(), viewport.name, viewport.desc));
    }
}

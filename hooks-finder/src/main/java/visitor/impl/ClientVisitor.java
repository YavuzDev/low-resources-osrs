package visitor.impl;

import hook.Hooks;
import hook.global.StaticFieldHook;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
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
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        if (descriptor.equalsIgnoreCase(correctType("Widget"))) {
            addStaticFieldHookIfNotContains("viewport", new StaticFieldHook(getCurrentClass().getName(), name, descriptor));
        }
        return super.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        var methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
        return methodVisitor;
    }
}

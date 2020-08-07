package visitor.impl;

import hook.local.FieldHook;
import hook.local.MethodHook;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import visitor.HookVisitor;
import visitor.condition.Condition;

import java.util.List;

public class GameEngineVisitor extends HookVisitor {

    @Override
    public List<Condition> conditions() {
        return List.of(fieldCondition(4, "Int"));
    }

    @Override
    public String className() {
        return "GameEngine";
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        addFieldHook("Field" + name, new FieldHook(name, descriptor));
        return super.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        addMethodHook("Method" + name, new MethodHook(name, descriptor));
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }
}

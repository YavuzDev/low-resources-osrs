package visitor;

import hook.ClassHook;
import hook.FieldHook;
import hook.Hooks;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import reader.ObfuscatedClass;
import visitor.condition.Condition;
import visitor.condition.FieldAmountCondition;

import java.util.List;

public abstract class HookVisitor extends ClassVisitor {

    private final Hooks hooks;

    private ObfuscatedClass currentClass;

    public HookVisitor() {
        super(Opcodes.ASM9);
        this.hooks = new Hooks();
    }

    public abstract List<Condition> conditions();

    public abstract String className();

    public Hooks getHooks() {
        return hooks;
    }

    public void addFieldHook(String givenName, FieldHook fieldHook) {
        hooks.getCorrectClass(currentClass).addFieldHook(givenName, fieldHook);
    }

    public FieldAmountCondition fieldCondition(int amount, String type) {
        return new FieldAmountCondition(amount, correctType(type));
    }

    private String correctType(String original) {
        return switch (original.toLowerCase()) {
            case "string" -> "Ljava/lang/String;";
            case "integer", "int" -> "I";
            default -> original;
        };
    }

    public void setCurrentClass(ObfuscatedClass currentClass) {
        this.currentClass = currentClass;
        if (!hooks.containsClass(currentClass)) {
            hooks.addClassHook(className(), new ClassHook(currentClass.getName()));
        }
    }

    @Override
    public String toString() {
        return "HookVisitor{" +
                "hooks=" + hooks +
                ", currentClass=" + currentClass +
                ", api=" + api +
                ", cv=" + cv +
                '}';
    }
}

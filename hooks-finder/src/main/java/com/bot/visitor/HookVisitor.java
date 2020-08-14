package com.bot.visitor;

import com.bot.hook.Hooks;
import com.bot.hook.global.StaticFieldHook;
import com.bot.hook.global.StaticMethodHook;
import com.bot.hook.local.ClassHook;
import com.bot.hook.local.FieldHook;
import com.bot.hook.local.MethodHook;
import com.bot.reader.ObfuscatedClass;
import com.bot.visitor.condition.*;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public abstract class HookVisitor extends ClassVisitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(HookVisitor.class);

    private Hooks hooks;

    private List<ObfuscatedClass> allClasses;

    private ObfuscatedClass currentClass;

    public HookVisitor() {
        super(Opcodes.ASM9);
    }

    public abstract List<Condition> conditions();

    public abstract void onSetClassNode();

    public Hooks getHooks() {
        return hooks;
    }

    public void addFieldHook(String givenName, FieldHook fieldHook) {
        LOGGER.info("Adding field hook with name {} and hook: {}", givenName, fieldHook);
        hooks.getCorrectClass(currentClass).addFieldHook(givenName, fieldHook);
    }

    public void addMethodHook(String givenName, MethodHook methodHook) {
        hooks.getCorrectClass(currentClass).addMethodHook(givenName, methodHook);
    }

    public void addStaticFieldHook(String givenName, StaticFieldHook staticFieldHook) {
        LOGGER.info("Adding static field hook with name {} and hook: {}", givenName, staticFieldHook);
        hooks.getStatics().addField(givenName, staticFieldHook);
    }

    public void addStaticFieldHookIfNotContains(String givenName, StaticFieldHook staticFieldHook) {
        if (hooks.containsStaticField(givenName)) {
            return;
        }
        hooks.getStatics().addField(givenName, staticFieldHook);
    }

    public void addStaticMethodHook(String givenName, StaticMethodHook staticMethodHook) {
        hooks.getStatics().addMethod(givenName, staticMethodHook);
    }

    public CallCountCondition callCountCondition(int count, String type) {
        return new CallCountCondition(count, correctType(type));
    }

    public StaticFieldAmountCondition staticFieldCondition(int access, String type) {
        return staticFieldCondition(1, access, type);
    }

    public StaticFieldAmountCondition staticFieldCondition(int amount, int access, String type) {
        return staticFieldCondition(amount, amount, access, type);
    }

    public StaticFieldAmountCondition staticFieldCondition(int min, int max, int access, String type) {
        return new StaticFieldAmountCondition(min, max, access, correctType(type));
    }

    public FieldAmountCondition fieldCondition(String type) {
        return fieldCondition(1, type);
    }

    public FieldAmountCondition fieldCondition(int amount, String type) {
        return fieldCondition(amount, amount, type);
    }

    public FieldAmountCondition fieldCondition(int min, int max, String type) {
        return new FieldAmountCondition(min, max, correctType(type));
    }

    public ParameterAmountCondition parameterCondition(String type) {
        return parameterCondition(1, type);
    }

    public ParameterAmountCondition parameterCondition(int amount, String type) {
        return parameterCondition(amount, amount, type);
    }

    public ParameterAmountCondition parameterCondition(int min, int max, String type) {
        return new ParameterAmountCondition(min, max, correctType(type));
    }

    public OpcodeCondition opcodeCondition(int opcode) {
        return new OpcodeCondition(opcode);
    }

    public String correctType(String original) {
        var builder = new StringBuilder();
        var lowerCase = original.toLowerCase();
        if (lowerCase.contains("array")) {
            builder.append("[");
        }
        var type = switch (lowerCase) {
            case "string" -> "Ljava/lang/String;";
            case "integer", "int", "integerarray" -> "I";
            case "boolean", "bool" -> "Z";
            case "long" -> "J";
            case "byte" -> "B";
            default -> "L" + Objects.requireNonNull(allClasses
                    .stream()
                    .filter(c -> c.getGivenName() != null)
                    .filter(c -> c.getGivenName().equalsIgnoreCase(original))
                    .findFirst()
                    .orElse(null))
                    .getName() + ";";
        };
        builder.append(type);
        return builder.toString();
    }

    public ObfuscatedClass getCurrentClass() {
        return currentClass;
    }

    public void setCurrentClass(ObfuscatedClass currentClass, String value) {
        this.currentClass = currentClass;
        this.currentClass.setGivenName(value);
        if (!hooks.containsClass(currentClass)) {
            hooks.addClassHook(value, new ClassHook(currentClass.getName()));
        }
        onSetClassNode();
    }

    public MethodNode getMethod(Condition... conditions) {
        var conditionCount = 0;
        for (var method : currentClass.getClassNode().methods) {
            for (var condition : conditions) {
                if (condition.check(method)) {
                    conditionCount++;
                }
            }
            if (conditionCount < conditions.length) {
                conditionCount = 0;
                continue;
            }
            LOGGER.info("Found method {} from conditions {} in class {} ", method.name, Arrays.toString(conditions), currentClass.getName());
            return method;
        }
        throw new NullPointerException("No function found with conditions: " + Arrays.toString(conditions));
    }

    public List<CallCountCondition.CallCountField> getFieldsFromCount(CallCountCondition callCountCondition) {
        for (var method : currentClass.getClassNode().methods) {
            callCountCondition.getCallCount(method);
        }
        var list = callCountCondition.getFields();
        LOGGER.info("Found fields {} from conditions {} in class {}", list, callCountCondition, currentClass.getName());
        return list;
    }

    public FieldInsnNode getField(MethodNode methodNode, Condition... conditions) {
        var conditionCount = 0;
        for (var instruction : methodNode.instructions) {
            if (!(instruction instanceof FieldInsnNode)) {
                continue;
            }
            var field = (FieldInsnNode) instruction;
            for (var condition : conditions) {
                if (condition.check(field)) {
                    conditionCount++;
                }
            }
            if (conditionCount < conditions.length) {
                conditionCount = 0;
                continue;
            }
            LOGGER.info("Found field {} from conditions {} in class {}", field.name, Arrays.toString(conditions), currentClass.getName());
            return field;
        }
        throw new NullPointerException("No field found with conditions: " + Arrays.toString(conditions) + " for method: " + methodNode);
    }

    public List<ObfuscatedClass> getAllClasses() {
        return allClasses;
    }

    public void setAllClasses(List<ObfuscatedClass> allClasses) {
        this.allClasses = allClasses;
    }

    public void setHooks(Hooks hooks) {
        this.hooks = hooks;
    }

    public String getOwner() {
        return currentClass.getName();
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

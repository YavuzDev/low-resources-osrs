package com.bot.visitor;

import com.bot.hook.Hooks;
import com.bot.hook.global.StaticFieldHook;
import com.bot.hook.global.StaticMethodHook;
import com.bot.hook.local.ClassHook;
import com.bot.hook.local.FieldHook;
import com.bot.hook.local.MethodHook;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.bot.reader.ObfuscatedClass;
import com.bot.visitor.condition.Condition;
import com.bot.visitor.condition.FieldAmountCondition;
import com.bot.visitor.condition.OpcodeCondition;
import com.bot.visitor.condition.ParameterAmountCondition;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public abstract class HookVisitor extends ClassVisitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(HookVisitor.class);

    private final Hooks hooks;

    private final List<ObfuscatedClass> allClasses;

    private ObfuscatedClass currentClass;

    public HookVisitor(Hooks hooks, List<ObfuscatedClass> allClasses) {
        super(Opcodes.ASM9);
        this.hooks = hooks;
        this.allClasses = allClasses;
    }

    public abstract List<Condition> conditions();

    public abstract String className();

    public abstract void onSetClassNode();

    public Hooks getHooks() {
        return hooks;
    }

    public void addFieldHook(String givenName, FieldHook fieldHook) {
        hooks.getCorrectClass(currentClass).addFieldHook(givenName, fieldHook);
    }

    public void addMethodHook(String givenName, MethodHook methodHook) {
        hooks.getCorrectClass(currentClass).addMethodHook(givenName, methodHook);
    }

    public void addStaticFieldHook(String givenName, StaticFieldHook staticFieldHook) {
        LOGGER.info("Adding static field com.bot.hook with name {} and com.bot.hook: {}", givenName, staticFieldHook);
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

    public FieldAmountCondition fieldCondition(int amount, String type) {
        return fieldCondition(amount, amount, type);
    }

    public FieldAmountCondition fieldCondition(int min, int max, String type) {
        return new FieldAmountCondition(min, max, correctType(type));
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
        return switch (original.toLowerCase()) {
            case "string" -> "Ljava/lang/String;";
            case "integer", "int" -> "I";
            case "boolean", "bool" -> "Z";
            default -> "L" + Objects.requireNonNull(allClasses
                    .stream()
                    .filter(c -> c.getGivenName() != null)
                    .filter(c -> c.getGivenName().equalsIgnoreCase(original))
                    .findFirst()
                    .orElse(null))
                    .getName() + ";";
        };
    }

    public ObfuscatedClass getCurrentClass() {
        return currentClass;
    }

    public void setCurrentClass(ObfuscatedClass currentClass) {
        this.currentClass = currentClass;
        this.currentClass.setGivenName(className());
        if (!hooks.containsClass(currentClass)) {
            hooks.addClassHook(className(), new ClassHook(currentClass.getName()));
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
            LOGGER.info("Found method {} from conditions {} ", method.name, Arrays.toString(conditions));
            return method;
        }
        throw new NullPointerException("No function found with conditions: " + Arrays.toString(conditions));
    }

    public FieldInsnNode getField(MethodNode methodNode, Condition... conditions) {
        var conditionCount = 0;
        for (var instruction : methodNode.instructions) {
            if (!(instruction instanceof FieldInsnNode)) {
                continue;
            }
            for (var condition : conditions) {
                if (condition.check(instruction)) {
                    conditionCount++;
                }
            }
            if (conditionCount < conditions.length) {
                conditionCount = 0;
                continue;
            }
            var field = (FieldInsnNode) instruction;
            LOGGER.info("Found field {} from conditions {} ", field.name, Arrays.toString(conditions));
            return field;
        }
        throw new NullPointerException("No static variable found with conditions: " + Arrays.toString(conditions) + " for method: " + methodNode);
    }

    public List<ObfuscatedClass> getAllClasses() {
        return allClasses;
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

package com.osrs.visitor.condition;

import com.osrs.reader.ObfuscatedClass;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class ParameterAmountCondition implements Condition {

    private final int min;

    private final int max;

    private final String type;

    public ParameterAmountCondition(int min, int max, String type) {
        if (min <= 0) {
            throw new IllegalArgumentException("Min has to be higher than 0");
        }
        if (max <= 0) {
            throw new IllegalArgumentException("Max has to be higher than 0");
        }
        if (max < min) {
            throw new IllegalArgumentException("Max has to be higher than min");
        }
        this.min = min;
        this.max = max;
        this.type = type;
    }

    @Override
    public boolean check(ObfuscatedClass obfuscatedClass) {
        throw new UnsupportedOperationException("ParameterCondition doesn't support checking for ObfuscatedClass");
    }

    @Override
    public boolean check(MethodNode methodNode) {
        var parameters = Type.getArgumentTypes(methodNode.desc);

        var found = 0;
        for (var parameter : parameters) {
            if (parameter.getDescriptor().equals(type)) {
                found++;
            }
        }
        return found >= min && found <= max;
    }

    @Override
    public boolean check(FieldInsnNode fieldInsnNode) {
        throw new UnsupportedOperationException("ParameterCondition doesn't support checking for FieldInsnNode");
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "ParameterCondition{" +
                "min=" + min +
                ", max=" + max +
                ", type='" + type + '\'' +
                '}';
    }
}

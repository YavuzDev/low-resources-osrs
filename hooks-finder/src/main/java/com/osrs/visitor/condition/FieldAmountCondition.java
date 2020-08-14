package com.osrs.visitor.condition;

import com.osrs.reader.ObfuscatedClass;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class FieldAmountCondition implements Condition {

    private final int min;

    private final int max;

    private final String type;

    public FieldAmountCondition(int min, int max, String type) {
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
        var fields = obfuscatedClass.getFields();

        var found = 0;
        for (var field : fields) {
            if (field.desc.equals(type)) {
                found++;
            }
        }
        return found >= min && found <= max;
    }

    @Override
    public boolean check(MethodNode methodNode) {
        throw new UnsupportedOperationException("FieldAmountCondition doesn't support checking for MethodNode");
    }

    @Override
    public boolean check(FieldInsnNode fieldInsnNode) {
        if (min > 1 || max > 1) {
            throw new UnsupportedOperationException("Min and max can't be higher than 1 for checking FieldInsnNode");
        }
        return fieldInsnNode.desc.equals(type);
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
        return "FieldAmountCondition{" +
                "min=" + min +
                ", max=" + max +
                ", type='" + type + '\'' +
                '}';
    }
}

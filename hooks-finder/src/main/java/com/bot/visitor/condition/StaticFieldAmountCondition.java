package com.bot.visitor.condition;

import com.bot.reader.ObfuscatedClass;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class StaticFieldAmountCondition implements Condition {

    private final int min;

    private final int max;

    private final int access;

    private final String type;

    public StaticFieldAmountCondition(int min, int max, int access, String type) {
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
        this.access = access;
        this.type = type;
    }

    @Override
    public boolean check(ObfuscatedClass obfuscatedClass) {
        var fields = obfuscatedClass.getFields();

        var found = 0;
        for (var field : fields) {
            if (field.desc.equals(type) && field.access == access) {
                found++;
            }
        }
        return found >= min && found <= max;
    }

    @Override
    public boolean check(MethodNode methodNode) {
        throw new UnsupportedOperationException("StaticFieldAmountCondition doesn't support checking for MethodNode");
    }

    @Override
    public boolean check(FieldInsnNode fieldInsnNode) {
        throw new UnsupportedOperationException("StaticFieldAmountCondition doesn't support checking for FieldInsnNode");
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public int getAccess() {
        return access;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "StaticFieldAmountCondition{" +
                "min=" + min +
                ", max=" + max +
                ", access=" + access +
                ", type='" + type + '\'' +
                '}';
    }
}

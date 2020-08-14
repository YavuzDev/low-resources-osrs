package com.osrs.visitor.condition;

import com.osrs.reader.ObfuscatedClass;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class CallCountCondition implements Condition {

    public static class CallCountField {

        private final String name;

        private final String type;

        public CallCountField(String name, String type) {
            this.name = name;
            this.type = type;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CallCountField that = (CallCountField) o;
            return Objects.equals(name, that.name) &&
                    Objects.equals(type, that.type);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, type);
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        @Override
        public String toString() {
            return "CallCountField{" +
                    "name='" + name + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }
    }

    private final int count;

    private final String type;

    private final Map<CallCountField, Integer> counts;

    public CallCountCondition(int count, String type) {
        this.count = count;
        this.type = type;
        this.counts = new HashMap<>();
    }

    @Override
    public boolean check(ObfuscatedClass obfuscatedClass) {
        throw new UnsupportedOperationException("CallCountCondition doesn't support checking for ObfuscatedClass");
    }

    @Override
    public boolean check(MethodNode methodNode) {
        throw new UnsupportedOperationException("CallCountCondition doesn't support checking for MethodNode");
    }

    @Override
    public boolean check(FieldInsnNode fieldInsnNode) {
        return counts.entrySet().stream().anyMatch(c -> c.getValue() == count);
    }

    public void getCallCount(MethodNode methodNode) {
        for (var instruction : methodNode.instructions) {
            if (!(instruction instanceof FieldInsnNode)) {
                continue;
            }
            var field = (FieldInsnNode) instruction;
            if (!field.desc.equals(type)) {
                continue;
            }
            var callCountField = new CallCountField(field.name, field.desc);
            counts.put(callCountField, counts.getOrDefault(callCountField, 0) + 1);
        }
    }

    public List<CallCountField> getFields() {
        return counts.entrySet()
                .stream()
                .filter(c -> c.getValue() == count)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }


    public int getCount() {
        return count;
    }

    public String getType() {
        return type;
    }

    public Map<CallCountField, Integer> getCounts() {
        return counts;
    }

    @Override
    public String toString() {
        return "CallCountCondition{" +
                "count=" + count +
                ", type='" + type + '\'' +
                '}';
    }
}

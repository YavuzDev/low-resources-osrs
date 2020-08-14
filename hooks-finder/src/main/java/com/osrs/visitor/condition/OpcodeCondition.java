package com.osrs.visitor.condition;

import com.osrs.reader.ObfuscatedClass;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class OpcodeCondition implements Condition {

    private final int opcode;

    public OpcodeCondition(int opcode) {
        this.opcode = opcode;
    }

    @Override
    public boolean check(ObfuscatedClass obfuscatedClass) {
        throw new UnsupportedOperationException("OpcodeCondition doesn't support checking for ObfuscatedClass");
    }

    @Override
    public boolean check(MethodNode methodNode) {
        throw new UnsupportedOperationException("OpcodeCondition doesn't support checking for MethodNode");
    }

    @Override
    public boolean check(FieldInsnNode fieldInsnNode) {
        return fieldInsnNode.getOpcode() == opcode;
    }

    @Override
    public String toString() {
        return "OpcodeCondition{" +
                "opcode=" + opcode +
                '}';
    }
}

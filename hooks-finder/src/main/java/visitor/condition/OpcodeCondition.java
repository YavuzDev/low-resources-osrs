package visitor.condition;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;
import reader.ObfuscatedClass;

public class OpcodeCondition implements Condition {

    private final int opcode;

    public OpcodeCondition(int opcode) {
        this.opcode = opcode;
    }

    @Override
    public boolean check(ObfuscatedClass obfuscatedClass) {
        throw new UnsupportedOperationException("OpcodeCondition doesn't support checking for classes");
    }

    @Override
    public boolean check(MethodNode methodNode) {
        throw new UnsupportedOperationException("OpcodeCondition doesn't support checking for MethodNode");
    }

    @Override
    public boolean check(AbstractInsnNode abstractInsnNode) {
        return abstractInsnNode.getOpcode() == opcode;
    }

    @Override
    public String toString() {
        return "OpcodeCondition{" +
                "opcode=" + opcode +
                '}';
    }
}

package visitor.condition;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;
import reader.ObfuscatedClass;

public interface Condition {

    boolean check(ObfuscatedClass obfuscatedClass);

    boolean check(MethodNode methodNode);

    boolean check(AbstractInsnNode abstractInsnNode);
}

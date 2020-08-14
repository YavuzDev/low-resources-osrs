package com.osrs.visitor.condition;

import com.osrs.reader.ObfuscatedClass;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;

public interface Condition {

    boolean check(ObfuscatedClass obfuscatedClass);

    boolean check(MethodNode methodNode);

    boolean check(FieldInsnNode fieldInsnNode);

}

package com.bot.visitor.condition;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;
import com.bot.reader.ObfuscatedClass;

public interface Condition {

    boolean check(ObfuscatedClass obfuscatedClass);

    boolean check(MethodNode methodNode);

    boolean check(AbstractInsnNode abstractInsnNode);
}

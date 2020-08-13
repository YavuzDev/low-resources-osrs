package com.bot.visitor.condition;

import com.bot.reader.ObfuscatedClass;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;

public interface Condition {

    boolean check(ObfuscatedClass obfuscatedClass);

    boolean check(MethodNode methodNode);

    boolean check(FieldInsnNode fieldInsnNode);

}

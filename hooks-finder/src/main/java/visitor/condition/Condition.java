package visitor.condition;

import reader.ObfuscatedClass;

public interface Condition {

    boolean check(ObfuscatedClass obfuscatedClass);
}

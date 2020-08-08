package com.bot.reader;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.List;

public class ObfuscatedClass {

    private final String name;

    private final ClassNode classNode;

    private String givenName;

    public ObfuscatedClass(String name, ClassNode classNode) {
        this.name = name;
        this.classNode = classNode;
    }

    public String getName() {
        return name;
    }

    public List<FieldNode> getFields() {
        return classNode.fields;
    }

    public ClassNode getClassNode() {
        return classNode;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    @Override
    public String toString() {
        return "ObfuscatedClass{" +
                "name='" + name + '\'' +
                ", classNode=" + classNode +
                ", givenName='" + givenName + '\'' +
                '}';
    }
}

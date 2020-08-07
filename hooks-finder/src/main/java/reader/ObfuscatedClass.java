package reader;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.List;

public class ObfuscatedClass {

    private final String name;

    private final ClassNode classNode;

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

    @Override
    public String toString() {
        return "ObfuscatedClass{" +
                "name='" + name + '\'' +
                ", classNode=" + classNode +
                '}';
    }
}

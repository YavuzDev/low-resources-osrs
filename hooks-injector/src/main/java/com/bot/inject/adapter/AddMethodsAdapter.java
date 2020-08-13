package com.bot.inject.adapter;

import com.bot.hook.Hooks;
import com.bot.hook.local.MethodHook;
import com.bot.inject.mixin.Copy;
import com.bot.inject.mixin.Mixin;
import com.bot.inject.mixin.Shadow;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.GeneratorAdapter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

public class AddMethodsAdapter extends ClassVisitor {

    private final Hooks hooks;

    private final Class<?> mixinClass;

    private final List<Method> methods;

    public AddMethodsAdapter(ClassVisitor classVisitor, Hooks hooks, Class<?> mixinClass, List<Method> methods) {
        super(Opcodes.ASM9, classVisitor);
        this.hooks = hooks;
        this.mixinClass = mixinClass;
        this.methods = methods;
    }

    @Override
    public void visitEnd() {
        try {
            var classReader = new ClassReader(mixinClass.getName());
            classReader.accept(new CopyMethodsVisitor(), ClassReader.EXPAND_FRAMES);
        } catch (IOException e) {
            e.printStackTrace();
        }

        super.visitEnd();
    }

    private class CopyMethodsVisitor extends ClassVisitor {

        public CopyMethodsVisitor() {
            super(Opcodes.ASM9);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            for (Method method : methods) {
                if (method.getName().equals(name) && Type.getMethodDescriptor(method).equals(descriptor)) {
                    var mv = AddMethodsAdapter.super.visitMethod(access, name, descriptor, signature, exceptions);
                    return new UpdateReferencesVisitor(mv, access, name, descriptor);
                }
            }
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }
    }

    private class UpdateReferencesVisitor extends GeneratorAdapter {

        public UpdateReferencesVisitor(MethodVisitor mv, int access, String name, String desc) {
            super(Opcodes.ASM9, mv, access, name, desc);
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
            if (owner.equals(Type.getType(mixinClass).getInternalName())) {
                var mixin = mixinClass.getAnnotation(Mixin.class);
                var classHook = hooks.getClassHook(mixin.value());
                Field field;
                try {
                    field = mixinClass.getDeclaredField(name);
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
                if (field.isAnnotationPresent(Shadow.class)) {
                    var shadow = field.getAnnotation(Shadow.class);
                    var fieldName = shadow.value();
                    if (fieldName.isEmpty()) {
                        fieldName = field.getName();
                    }
                    if (Modifier.isStatic(field.getModifiers())) {
                        var fieldHook = hooks.getStaticField(fieldName);
                        if (fieldHook == null) {
                            throw new IllegalStateException("No static field hook found for shadow field " + fieldName);
                        }
                        owner = fieldHook.getOwner();
                        name = fieldHook.getName();
                        descriptor = Type.getType(fieldHook.getType()).getDescriptor();
                    } else {
                        var fieldHook = classHook.getField(fieldName);
                        if (fieldHook == null) {
                            throw new IllegalStateException("No field hook found for shadow field " + fieldName);
                        }
                        owner = classHook.getName();
                        name = fieldHook.getName();
                        descriptor = Type.getType(fieldHook.getType()).getDescriptor();
                    }
                } else {
                    owner = classHook.getName();
                }
            }
            super.visitFieldInsn(opcode, owner, name, descriptor);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            if (owner.equals(Type.getType(mixinClass).getInternalName())) {
                var mixin = mixinClass.getAnnotation(Mixin.class);
                var classHook = hooks.getClassHook(mixin.value());
                var isStatic = opcode == Opcodes.INVOKESTATIC;
                owner = classHook.getName();
                Method method = null;
                for (var m : mixinClass.getDeclaredMethods()) {
                    if (m.getName().equals(name) && Type.getMethodDescriptor(m).equals(descriptor)) {
                        method = m;
                        break;
                    }
                }
                if (method != null) {
                    String hookName = null;
                    if (method.isAnnotationPresent(Copy.class)) {
                        hookName = method.getAnnotation(Copy.class).value();
                    }
                    if (hookName != null) {
                        Integer dummyValue;
                        Type[] argTypes = Type.getArgumentTypes(descriptor);
                        if (isStatic) {
                            var methodHook = hooks.getStaticMethod(hookName);
                            if (methodHook == null) {
                                throw new IllegalStateException("No static method hook found for " + hookName);
                            }
                            descriptor = methodHook.getType();
                            dummyValue = null;//TODO check this
                        } else {
                            MethodHook methodHook = classHook.getMethod(hookName);
                            if (methodHook == null) {
                                throw new IllegalStateException("No method hook found for " + mixin.value() + "."
                                        + hookName);
                            }
                            descriptor = methodHook.getType();
                            dummyValue = null;//TODO check this
                        }
                        Type[] newArgTypes = Type.getArgumentTypes(descriptor);
                        if (!Arrays.equals(argTypes, newArgTypes)) {
                            var locals = new int[argTypes.length];
                            for (int i = 0; i < locals.length; i++) {
                                locals[i] = newLocal(argTypes[i]);
                            }
                            for (int i = locals.length - 1; i >= 0; i--) {
                                storeLocal(locals[i]);
                            }
                            for (int i = 0; i < locals.length; i++) {
                                var type = argTypes[i];
                                var newType = newArgTypes[i];
                                loadLocal(locals[i]);
                                if (!type.equals(newType)) {
                                    checkCast(newType);
                                }
                            }
                        }
                        if (dummyValue != null) {
                            push(dummyValue);
                        }
                    }
                }
            }
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }
    }
}

package com.bot;

import com.bot.visitor.HookVisitor;

import java.util.Arrays;
import java.util.Objects;

public class DependentVisitor {

    private final HookVisitor hookVisitor;

    private final Class<?>[] dependents;

    public DependentVisitor(HookVisitor hookVisitor, Class<?>[] dependents) {
        this.hookVisitor = hookVisitor;
        this.dependents = dependents;
    }

    public HookVisitor getHookVisitor() {
        return hookVisitor;
    }

    public Class<?>[] getDependents() {
        return dependents;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DependentVisitor that = (DependentVisitor) o;
        return Objects.equals(hookVisitor, that.hookVisitor) &&
                Arrays.equals(dependents, that.dependents);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(hookVisitor);
        result = 31 * result + Arrays.hashCode(dependents);
        return result;
    }

    @Override
    public String toString() {
        return "com.bot.DependentVisitor{" +
                "hookVisitor=" + hookVisitor +
                ", dependents=" + Arrays.toString(dependents) +
                '}';
    }
}

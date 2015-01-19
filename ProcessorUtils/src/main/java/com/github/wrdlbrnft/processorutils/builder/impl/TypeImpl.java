package com.github.wrdlbrnft.processorutils.builder.impl;

import java.util.Set;

import javax.lang.model.element.Modifier;

import com.github.wrdlbrnft.processorutils.builder.api.elements.Type;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 09/12/14
 */
class TypeImpl implements Type {

    private final String mPackageName;
    private final String mClassName;
    private final String mFullClassName;
    private final Set<Modifier> mModifiers;
    private final Type mParent;

    public TypeImpl(String packageName, String className, Set<Modifier> modifiers, Type parent) {
        mPackageName = packageName;
        mClassName = className;
        mFullClassName = packageName != null && packageName.length() > 0 ? String.format("%s.%s", packageName, className) : className;
        mModifiers = modifiers;
        mParent = parent;
    }

    @Override
    public String packageName() {
        return mPackageName;
    }

    @Override
    public String className() {
        return mClassName;
    }

    @Override
    public String fullClassName() {
        return mFullClassName;
    }

    @Override
    public Type parent() {
        return mParent;
    }

    @Override
    public Set<Modifier> modifiers() {
        return mModifiers;
    }

    @Override
    public boolean isSubtypeOf(Type type) {
        return this.equals(type) || mParent != null && mParent.isSubtypeOf(type);
    }

    @Override
    public String toString() {
        return mFullClassName;
    }

    @Override
    public boolean equals(Object obj) {

        if(obj instanceof Type) {
            final Type otherType = (Type) obj;
            final String otherClassName = otherType.fullClassName();
            return mFullClassName.equals(otherClassName);
        }

        return false;
    }
}

package com.github.wrdlbrnft.processorutils.builder.impl;

import java.util.Set;

import javax.lang.model.element.Modifier;

import com.github.wrdlbrnft.processorutils.builder.api.code.CodeBlock;
import com.github.wrdlbrnft.processorutils.builder.api.elements.Executable;
import com.github.wrdlbrnft.processorutils.builder.api.elements.Variable;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 21/12/14
 */
class ExecutableImpl implements Executable {

    private final Set<Variable> mParameters;
    private final Set<Modifier> mModifiers;
    private final CodeBlockImpl mCode = new CodeBlockImpl();

    public ExecutableImpl(Set<Variable> parameters, Set<Modifier> modifiers) {
        mParameters = parameters;
        mModifiers = modifiers;
    }

    @Override
    public Set<Variable> parameters() {
        return mParameters;
    }

    @Override
    public Set<Modifier> modifiers() {
        return mModifiers;
    }

    @Override
    public CodeBlock code() {
        return mCode;
    }
}

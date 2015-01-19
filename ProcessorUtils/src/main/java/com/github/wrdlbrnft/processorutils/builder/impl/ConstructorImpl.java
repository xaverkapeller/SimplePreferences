package com.github.wrdlbrnft.processorutils.builder.impl;

import java.util.Set;

import javax.lang.model.element.Modifier;

import com.github.wrdlbrnft.processorutils.builder.api.elements.Constructor;
import com.github.wrdlbrnft.processorutils.builder.api.elements.Variable;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 21/12/14
 */
class ConstructorImpl extends ExecutableImpl implements Constructor {

    public ConstructorImpl(Set<Variable> parameters, Set<Modifier> modifiers) {
        super(parameters, modifiers);
    }
}

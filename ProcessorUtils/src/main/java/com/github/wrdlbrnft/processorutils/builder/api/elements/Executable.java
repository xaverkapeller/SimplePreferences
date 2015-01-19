package com.github.wrdlbrnft.processorutils.builder.api.elements;

import java.util.Set;

import javax.lang.model.element.Modifier;

import com.github.wrdlbrnft.processorutils.builder.api.code.CodeBlock;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 21/12/14
 */
public interface Executable {
    public Set<Variable> parameters();
    public Set<Modifier> modifiers();
    public CodeBlock code();
}

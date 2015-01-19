package com.github.wrdlbrnft.processorutils.builder.api.builder;

import java.util.Set;

import com.github.wrdlbrnft.processorutils.builder.api.code.CodeBlock;
import com.github.wrdlbrnft.processorutils.builder.api.elements.Variable;
import com.github.wrdlbrnft.processorutils.builder.impl.VariableGenerator;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 28/12/14
 */
public interface ExecutableBuilder {
    public Set<Variable> createParameterSet(VariableGenerator generator);
    public void writeBody(CodeBlock code, VariableGenerator generator);
}

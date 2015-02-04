package com.github.wrdlbrnft.preferences.builder.api.builder;


import com.github.wrdlbrnft.preferences.builder.api.code.CodeBlock;
import com.github.wrdlbrnft.preferences.builder.api.elements.Variable;
import com.github.wrdlbrnft.preferences.builder.impl.VariableGenerator;

import java.util.List;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 28/12/14
 */
public interface ExecutableBuilder {
    public List<Variable> createParameterList(VariableGenerator generator);
    public void writeBody(CodeBlock code, VariableGenerator generator);
}

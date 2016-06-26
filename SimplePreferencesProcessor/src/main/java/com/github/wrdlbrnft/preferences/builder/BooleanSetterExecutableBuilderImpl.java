package com.github.wrdlbrnft.preferences.builder;

import com.github.wrdlbrnft.codebuilder.code.Block;
import com.github.wrdlbrnft.codebuilder.executables.ExecutableBuilder;
import com.github.wrdlbrnft.codebuilder.variables.Field;
import com.github.wrdlbrnft.codebuilder.types.Types;
import com.github.wrdlbrnft.codebuilder.elements.values.Values;
import com.github.wrdlbrnft.codebuilder.variables.Variable;
import com.github.wrdlbrnft.codebuilder.variables.Variables;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kapeller on 08/07/15.
 */
class BooleanSetterExecutableBuilderImpl extends ExecutableBuilder {

    private final Field mFieldPreferences;
    private final String mKey;

    private Variable parameter;

    public BooleanSetterExecutableBuilderImpl(Field fieldPreferences, String key) {
        mFieldPreferences = fieldPreferences;
        mKey = key;
    }

    @Override
    protected List<Variable> createParameters() {
        final List<Variable> parameters = new ArrayList<>();

        parameters.add(parameter = Variables.of(Types.of(boolean.class)));

        return parameters;
    }

    @Override
    protected void write(Block block) {
        block.append(mFieldPreferences).append(".edit().putBoolean(").append(Values.of(mKey)).append(", ").append(parameter).append(").apply();");
    }
}

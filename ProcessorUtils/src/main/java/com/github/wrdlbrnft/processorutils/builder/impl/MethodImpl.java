package com.github.wrdlbrnft.processorutils.builder.impl;

import java.util.Set;

import javax.lang.model.element.Modifier;

import com.github.wrdlbrnft.processorutils.builder.api.elements.Method;
import com.github.wrdlbrnft.processorutils.builder.api.elements.Type;
import com.github.wrdlbrnft.processorutils.builder.api.elements.Variable;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 09/12/14
 */
class MethodImpl extends ExecutableImpl implements Method {

    private final String mName;
    private final Type mReturnType;

    public MethodImpl(Type returnType, String name, Set<Variable> parameters, Set<Modifier> modifiers) {
        super(parameters, modifiers);
        mReturnType = returnType;
        mName = name;
    }

    @Override
    public String name() {
        return mName;
    }

    @Override
    public Type returnType() {
        return mReturnType;
    }

    @Override
    public String execute(String target, Variable... parameters) {
        final Variable[] methodParameters = parameters().toArray(new Variable[parameters().size()]);
        if (methodParameters.length != parameters.length) {
            throw new IllegalStateException("Supplied too many or too few arguments for method " + mName);
        }

        final StringBuilder builder = new StringBuilder();

        if(target != null) {
            builder.append(target).append(".");
        }

        builder.append(mName).append("(");

        for (int i = 0, length = methodParameters.length; i < length; i++) {
            if (i > 0) {
                builder.append(", ");
            }

            final Variable methodParameter = methodParameters[i];
            final Variable suppliedParameter = parameters[i];

            if (suppliedParameter.type().isSubtypeOf(methodParameter.type())) {
                builder.append(suppliedParameter.name());
            } else {
                throw new IllegalStateException("The supplied parameter " + suppliedParameter.name() + " cannot be applied to " + methodParameter.type().fullClassName());
            }
        }

        builder.append(")");

        return builder.toString();
    }
}

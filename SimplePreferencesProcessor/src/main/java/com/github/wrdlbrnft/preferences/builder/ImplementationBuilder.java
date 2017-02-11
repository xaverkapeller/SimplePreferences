package com.github.wrdlbrnft.preferences.builder;

import com.github.wrdlbrnft.codebuilder.annotations.Annotations;
import com.github.wrdlbrnft.codebuilder.code.Block;
import com.github.wrdlbrnft.codebuilder.code.SourceFile;
import com.github.wrdlbrnft.codebuilder.executables.Constructor;
import com.github.wrdlbrnft.codebuilder.executables.ExecutableBuilder;
import com.github.wrdlbrnft.codebuilder.executables.Method;
import com.github.wrdlbrnft.codebuilder.implementations.Implementation;
import com.github.wrdlbrnft.codebuilder.types.Type;
import com.github.wrdlbrnft.codebuilder.types.Types;
import com.github.wrdlbrnft.codebuilder.util.Utils;
import com.github.wrdlbrnft.codebuilder.variables.Field;
import com.github.wrdlbrnft.codebuilder.variables.Variable;
import com.github.wrdlbrnft.codebuilder.variables.Variables;
import com.github.wrdlbrnft.preferences.analyzer.GetterSetterPair;
import com.github.wrdlbrnft.preferences.analyzer.PreferencesAnalyzerResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

/**
 * Created by kapeller on 08/07/15.
 */
public class ImplementationBuilder {

    private final ProcessingEnvironment mProcessingEnvironment;

    private Field mFieldPreferences;
    private Field mFieldContext;

    public ImplementationBuilder(ProcessingEnvironment processingEnvironment) {
        mProcessingEnvironment = processingEnvironment;
    }

    public Implementation build(PreferencesAnalyzerResult result) throws IOException {
        final TypeElement interfaceElement = result.getInterfaceElement();
        final Implementation.Builder preferencesBuilder = new Implementation.Builder();
        preferencesBuilder.addImplementedType(Types.of(interfaceElement));
        preferencesBuilder.setModifiers(EnumSet.of(Modifier.PRIVATE, Modifier.STATIC ,Modifier.FINAL));

        mFieldContext = new Field.Builder()
                .setType(Types.Android.CONTEXT)
                .setModifiers(EnumSet.of(Modifier.PRIVATE, Modifier.FINAL))
                .build();
        preferencesBuilder.addField(mFieldContext);

        mFieldPreferences = new Field.Builder()
                .setType(Types.Android.SHARED_PREFERENCES)
                .setModifiers(EnumSet.of(Modifier.PRIVATE, Modifier.FINAL))
                .build();
        preferencesBuilder.addField(mFieldPreferences);

        final Constructor constructor = new Constructor.Builder()
                .setModifiers(EnumSet.of(Modifier.PUBLIC))
                .setCode(new ExecutableBuilder() {

                    private Variable paramContext;
                    private Variable paramPreferences;

                    @Override
                    protected List<Variable> createParameters() {
                        final List<Variable> parameters = new ArrayList<>();

                        parameters.add(paramContext = Variables.of(Types.Android.CONTEXT));
                        parameters.add(paramPreferences = Variables.of(Types.Android.SHARED_PREFERENCES));

                        return parameters;
                    }

                    @Override
                    protected void write(Block block) {
                        block.set(mFieldContext, paramContext).append(";").newLine();
                        block.set(mFieldPreferences, paramPreferences).append(";");
                    }
                })
                .build();
        preferencesBuilder.addConstructor(constructor);

        final List<GetterSetterPair> pairs = result.getPairs();
        for (GetterSetterPair pair : pairs) {
            final String key = pair.getKey();
            final TypeMirror setterType = pair.getSetterType();
            final ExecutableElement setterElement = pair.getSetterElement();
            final ExecutableElement getterElement = pair.getGetterElement();

            if (getterElement != null) {
                final ExecutableBuilder builder = getGetterExecutableBuilder(getterElement, key);
                preferencesBuilder.addMethod(implementMethod(getterElement, builder));
            }
            if (setterElement != null) {
                final ExecutableBuilder builder = getSetterExecutableBuilder(setterType, key);
                preferencesBuilder.addMethod(implementMethod(setterElement, builder));
            }
        }

        return preferencesBuilder.build();
    }

    private Method implementMethod(ExecutableElement method, ExecutableBuilder builder) {
        return new Method.Builder()
                .setModifiers(EnumSet.of(Modifier.PUBLIC))
                .addAnnotation(Annotations.forType(Override.class))
                .setReturnType(Types.of(method.getReturnType()))
                .setName(method.getSimpleName().toString())
                .setCode(builder)
                .build();
    }

    private ExecutableBuilder getGetterExecutableBuilder(ExecutableElement method, String key) {
        final TypeMirror returnType = method.getReturnType();

        if (Utils.isSameType(returnType, String.class)) {
            return new StringGetterExecutableBuilderImpl(mProcessingEnvironment, method, mFieldContext, mFieldPreferences, key);
        }
        if (Utils.isSameType(returnType, int.class)) {
            return new IntegerGetterExecutableBuilderImpl(mProcessingEnvironment, method, mFieldPreferences, key);
        }
        if (Utils.isSameType(returnType, long.class)) {
            return new LongGetterExecutableBuilderImpl(mProcessingEnvironment, method, mFieldPreferences, key);
        }
        if (Utils.isSameType(returnType, boolean.class)) {
            return new BooleanGetterExecutableBuilderImpl(mProcessingEnvironment, method, mFieldPreferences, key);
        }
        if (Utils.isSameType(returnType, float.class)) {
            return new FloatGetterExecutableBuilderImpl(mProcessingEnvironment, method, mFieldPreferences, key);
        }

        throw new UnsupportedOperationException("The type " + returnType + " is not supported for getters!");
    }

    private ExecutableBuilder getSetterExecutableBuilder(TypeMirror parameterType, String key) {
        if (Utils.isSameType(parameterType, String.class)) {
            return new StringSetterExecutableBuilderImpl(mFieldPreferences, key);
        }
        if (Utils.isSameType(parameterType, int.class)) {
            return new IntegerSetterExecutableBuilderImpl(mFieldPreferences, key);
        }
        if (Utils.isSameType(parameterType, long.class)) {
            return new LongSetterExecutableBuilderImpl(mFieldPreferences, key);
        }
        if (Utils.isSameType(parameterType, boolean.class)) {
            return new BooleanSetterExecutableBuilderImpl(mFieldPreferences, key);
        }
        if (Utils.isSameType(parameterType, float.class)) {
            return new FloatSetterExecutableBuilderImpl(mFieldPreferences, key);
        }

        throw new UnsupportedOperationException("The type " + parameterType + " is not supported for setters!");
    }
}

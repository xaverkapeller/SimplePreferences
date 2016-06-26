package com.github.wrdlbrnft.preferences.builder;

import com.github.wrdlbrnft.codebuilder.code.Block;
import com.github.wrdlbrnft.codebuilder.code.BlockWriter;
import com.github.wrdlbrnft.codebuilder.code.CodeElement;
import com.github.wrdlbrnft.codebuilder.executables.ExecutableBuilder;
import com.github.wrdlbrnft.codebuilder.variables.Field;
import com.github.wrdlbrnft.codebuilder.util.Utils;
import com.github.wrdlbrnft.codebuilder.elements.values.Values;
import com.github.wrdlbrnft.codebuilder.variables.Variable;
import com.github.wrdlbrnft.preferences.SimplePreferencesAnnotations;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.tools.Diagnostic;

/**
 * Created by kapeller on 08/07/15.
 */
class StringGetterExecutableBuilderImpl extends ExecutableBuilder {

    private static final String[] RESOURCE_BLACKLIST = new String[]{
            SimplePreferencesAnnotations.ANNOTATION_DEFAULT_STRING_VALUE,
            SimplePreferencesAnnotations.ANNOTATION_DEFAULT_BOOLEAN_VALUE,
            SimplePreferencesAnnotations.ANNOTATION_DEFAULT_FLOAT_VALUE,
            SimplePreferencesAnnotations.ANNOTATION_DEFAULT_INTEGER_VALUE,
            SimplePreferencesAnnotations.ANNOTATION_DEFAULT_LONG_VALUE
    };

    private static final String[] STRING_BLACKLIST = new String[]{
            SimplePreferencesAnnotations.ANNOTATION_DEFAULT_BOOLEAN_VALUE,
            SimplePreferencesAnnotations.ANNOTATION_DEFAULT_FLOAT_VALUE,
            SimplePreferencesAnnotations.ANNOTATION_DEFAULT_INTEGER_VALUE,
            SimplePreferencesAnnotations.ANNOTATION_DEFAULT_LONG_VALUE,
            SimplePreferencesAnnotations.ANNOTATION_DEFAULT_RESOURCE_VALUE
    };

    private final ProcessingEnvironment mProcessingEnvironment;
    private final ExecutableElement mMethod;
    private final Field mFieldContext;
    private final Field mFieldPreferences;
    private final String mKey;

    public StringGetterExecutableBuilderImpl(ProcessingEnvironment processingEnvironment, ExecutableElement method, Field fieldContext, Field fieldPreferences, String key) {
        mProcessingEnvironment = processingEnvironment;
        mMethod = method;
        mFieldContext = fieldContext;
        mFieldPreferences = fieldPreferences;
        mKey = key;
    }

    @Override
    protected List<Variable> createParameters() {
        return new ArrayList<>();
    }

    @Override
    protected void write(Block block) {
        final CodeElement defaultBlock = getDefaultElement();
        block.append("return ").append(mFieldPreferences).append(".getString(").append(Values.of(mKey)).append(", ").append(defaultBlock).append(");");
    }

    private CodeElement getDefaultElement() {
        if (Utils.hasAnnotation(mMethod, SimplePreferencesAnnotations.ANNOTATION_DEFAULT_STRING_VALUE)) {
            return getDefaultStringValueElement();
        } else if (Utils.hasAnnotation(mMethod, SimplePreferencesAnnotations.ANNOTATION_DEFAULT_RESOURCE_VALUE)) {
            return getDefaultResourceValueElement();
        }

        if (Utils.hasOneAnnotationOf(mMethod, STRING_BLACKLIST)) {
            mProcessingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, "Invalid default value annotation found!", mMethod);
        }
        return Values.ofNull();
    }

    @SuppressWarnings("ConstantConditions")
    private CodeElement getDefaultResourceValueElement() {
        if (Utils.hasOneAnnotationOf(mMethod, RESOURCE_BLACKLIST)) {
            mProcessingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, "Invalid default value annotation found!", mMethod);
        }

        final int resId = (int) Utils.getAnnotationValue(mMethod, SimplePreferencesAnnotations.ANNOTATION_DEFAULT_RESOURCE_VALUE, "value").getValue();
        return new BlockWriter() {
            @Override
            protected void write(Block block) {
                block.append(mFieldContext).append(".getString(").append(Values.of(resId)).append(")");
            }
        };
    }

    @SuppressWarnings("ConstantConditions")
    private CodeElement getDefaultStringValueElement() {
        if (Utils.hasOneAnnotationOf(mMethod, STRING_BLACKLIST)) {
            mProcessingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, "Invalid default value annotation found!", mMethod);
        }

        final String defaultValue = (String) Utils.getAnnotationValue(mMethod, SimplePreferencesAnnotations.ANNOTATION_DEFAULT_STRING_VALUE, "value").getValue();
        return Values.of(defaultValue);
    }
}

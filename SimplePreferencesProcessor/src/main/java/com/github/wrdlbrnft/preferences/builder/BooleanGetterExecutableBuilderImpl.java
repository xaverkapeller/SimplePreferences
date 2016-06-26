package com.github.wrdlbrnft.preferences.builder;

import com.github.wrdlbrnft.codebuilder.code.Block;
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
class BooleanGetterExecutableBuilderImpl extends ExecutableBuilder {

    private static final String[] BOOLEAN_BLACKLIST = new String[]{
            SimplePreferencesAnnotations.ANNOTATION_DEFAULT_STRING_VALUE,
            SimplePreferencesAnnotations.ANNOTATION_DEFAULT_FLOAT_VALUE,
            SimplePreferencesAnnotations.ANNOTATION_DEFAULT_INTEGER_VALUE,
            SimplePreferencesAnnotations.ANNOTATION_DEFAULT_LONG_VALUE,
            SimplePreferencesAnnotations.ANNOTATION_DEFAULT_RESOURCE_VALUE
    };

    private final ProcessingEnvironment mProcessingEnvironment;
    private final ExecutableElement mMethod;
    private final Field mFieldPreferences;
    private final String mKey;

    public BooleanGetterExecutableBuilderImpl(ProcessingEnvironment processingEnvironment, ExecutableElement method, Field fieldPreferences, String key) {
        mProcessingEnvironment = processingEnvironment;
        mMethod = method;
        mFieldPreferences = fieldPreferences;
        mKey = key;
    }

    @Override
    protected List<Variable> createParameters() {
        return new ArrayList<>();
    }

    @Override
    protected void write(Block block) {
        if (Utils.hasOneAnnotationOf(mMethod, BOOLEAN_BLACKLIST)) {
            mProcessingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, "Invalid default value annotation found!", mMethod);
        }

        final CodeElement defaultValue = getDefaultElement();
        block.append("return ").append(mFieldPreferences).append(".getBoolean(").append(Values.of(mKey)).append(", ").append(defaultValue).append(");");
    }

    @SuppressWarnings("ConstantConditions")
    private CodeElement getDefaultElement() {
        if (Utils.hasAnnotation(mMethod, SimplePreferencesAnnotations.ANNOTATION_DEFAULT_BOOLEAN_VALUE)) {
            final boolean defaultValue = (boolean) Utils.getAnnotationValue(mMethod, SimplePreferencesAnnotations.ANNOTATION_DEFAULT_BOOLEAN_VALUE, "value").getValue();
            return Values.of(defaultValue);
        }

        return Values.of(false);
    }
}

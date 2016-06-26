package com.github.wrdlbrnft.preferences.builder;

import com.github.wrdlbrnft.codebuilder.code.Block;
import com.github.wrdlbrnft.codebuilder.code.CodeElement;
import com.github.wrdlbrnft.codebuilder.elements.values.Values;
import com.github.wrdlbrnft.codebuilder.executables.ExecutableBuilder;
import com.github.wrdlbrnft.codebuilder.util.Utils;
import com.github.wrdlbrnft.codebuilder.variables.Field;
import com.github.wrdlbrnft.codebuilder.variables.Variable;
import com.github.wrdlbrnft.preferences.SimplePreferencesAnnotations;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.tools.Diagnostic;

/**
 * Created by kapeller on 13/01/16.
 */
class GetterExecutableBuilder<T> extends ExecutableBuilder {

    private final ProcessingEnvironment mProcessingEnvironment;
    private final String mDefaultValueAnnotation;
    private final ExecutableElement mMethod;
    private final Field mFieldPreferences;
    private final String mKey;

    public GetterExecutableBuilder(ProcessingEnvironment processingEnvironment, String defaultValueAnnotation, ExecutableElement method, Field fieldPreferences, String key) {
        mProcessingEnvironment = processingEnvironment;
        mDefaultValueAnnotation = defaultValueAnnotation;
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

        for (String annotation : SimplePreferencesAnnotations.ANNOTATIONS) {
            if (!mDefaultValueAnnotation.equals(annotation) && Utils.hasOneAnnotationOf(mMethod, SimplePreferencesAnnotations.ANNOTATIONS)) {
                mProcessingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, "Invalid default value annotation found!", mMethod);
                return;
            }
        }

        final CodeElement defaultValue = getDefaultElement();
        block.append("return ").append(mFieldPreferences).append(".getFloat(").append(Values.of(mKey)).append(", ").append(defaultValue).append(");");
    }

    @SuppressWarnings("ConstantConditions")
    private CodeElement getDefaultElement() {
        if (Utils.hasAnnotation(mMethod, SimplePreferencesAnnotations.ANNOTATION_DEFAULT_FLOAT_VALUE)) {
            final float defaultValue = (float) Utils.getAnnotationValue(mMethod, SimplePreferencesAnnotations.ANNOTATION_DEFAULT_FLOAT_VALUE, "value").getValue();
            return Values.of(defaultValue);
        }

        return Values.of(0.0f);
    }
}

package com.github.wrdlbrnft.preferences.analyzer;

import com.github.wrdlbrnft.codebuilder.util.Utils;
import com.github.wrdlbrnft.preferences.Constants;
import com.github.wrdlbrnft.preferences.SimplePreferencesAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

/**
 * Created by kapeller on 10/07/15.
 */
public class PreferencesAnalyzer {

    private final Map<String, GetterSetterPair> mPairMap = new HashMap<>();
    private final ProcessingEnvironment mProcessingEnvironment;

    public PreferencesAnalyzer(ProcessingEnvironment environment) {
        mProcessingEnvironment = environment;
    }

    public PreferencesAnalyzerResult analyze(TypeElement element) {
        mPairMap.clear();

        if (element.getKind() != ElementKind.INTERFACE) {
            mProcessingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, element.getSimpleName() + " is not an interface! SimplePreferences exclusively works with interfaces! If you don't know how to use this library than please refer to the documentation.", element);
            return null;
        }

        final List<? extends Element> members = element.getEnclosedElements();
        for (final Element member : members) {
            if (member.getKind() == ElementKind.METHOD) {
                analyzeMethod(element, (ExecutableElement) member);
            }
        }

        for (GetterSetterPair pair : mPairMap.values()) {
            final ExecutableElement getterElement = pair.getGetterElement();
            final ExecutableElement setterElement = pair.getSetterElement();

            switch (pair.checkIt()) {

                case OK:
                    break;

                case TYPE_MISMATCH:
                    mProcessingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, "The return type of the getter " + getterElement.getSimpleName() + " does not match the parameter type of " + pair.getGetterElement().getSimpleName(), getterElement);
                    break;

                case NO_GETTER:
                    mProcessingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, "No corresponding getter could be found for " + setterElement.getSimpleName() + "! Check your spelling or add a valid getter!", setterElement);
                    break;

                case NO_SETTER:
                    mProcessingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, "No corresponding setter could be found for " + getterElement.getSimpleName() + "! Check your spelling or add a valid setter!", getterElement);
                    break;

                default:
                    mProcessingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unhandled case encountered when analyzing getter/setter pair!");
                    break;
            }
        }

        final List<GetterSetterPair> pairs = new ArrayList<>(mPairMap.values());
        final String sharedPreferencesName = getSharedPreferencesName(element);
        return new PreferencesAnalyzerResult(element, pairs, sharedPreferencesName);
    }

    private String getSharedPreferencesName(TypeElement element) {
        final AnnotationValue value = Utils.getAnnotationValue(element, SimplePreferencesAnnotations.PREFERENCES, "value");
        return String.valueOf(value.getValue());
    }

    private void analyzeMethod(TypeElement element, ExecutableElement method) {
        final String name = getMethodName(method);
        final TypeMirror returnType = method.getReturnType();

        if (name.length() < 4) {
            mProcessingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, "The method " + name + " in " + element.getSimpleName() + "is not a valid getter or setter method.", method);
        }

        if (!isValidType(returnType)) {
            mProcessingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, "The return type of " + name + " in " + element.getSimpleName() + " is not a valid. Only int, long, float, boolean, String and void are allowed.", method);
        }

        if ("set".equals(name.substring(0, 3))) {
            analyzeSet(element, method);
        } else if ("get".equals(name.substring(0, 3))) {
            analyzeGet(element, method);
        } else if ("is".equals(name.substring(0, 2)) && Character.isUpperCase(name.charAt(2))) {
            analyzeIs(element, method);
        } else {
            mProcessingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, "The method " + name + " of " + element.getSimpleName() + "is not a valid getter or setter method! Expected method name to start with \"set\", \"get\" or \"is\"!");
        }
    }

    private void analyzeSet(TypeElement element, ExecutableElement method) {
        final String name = getMethodName(method);
        final TypeMirror returnType = method.getReturnType();
        final List<? extends VariableElement> methodParameters = method.getParameters();

        if (methodParameters.size() != 1) {
            mProcessingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, "The method " + name + " in " + element.getSimpleName() + " is a setter and therefore requires exactly one parameter.", method);
            return;
        }

        final VariableElement parameterElement = methodParameters.get(0);
        final TypeMirror parameterType = parameterElement.asType();

        if (!isValidType(parameterType)) {
            mProcessingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, "The parameter type of " + name + " in " + element.getSimpleName() + " is not a valid. Only int, long, float, boolean and String are allowed.", parameterElement);
        }

        if (!Utils.isSameType(returnType, void.class)) {
            mProcessingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, "The method " + name + " in " + element.getSimpleName() + " is a setter but it returns a value! Setters need to have a void return type!");
            return;
        }

        if (Utils.hasOneAnnotationOf(method, SimplePreferencesAnnotations.ANNOTATIONS)) {
            mProcessingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, "Invalid annotation found! You can only annotate getter methods with default value annotations!", method);
            return;
        }

        final String key = name.substring(3, name.length());
        final GetterSetterPair pair = getGetterSetterPair(key);
        pair.setSetter(method, parameterType);
    }

    private void analyzeGet(TypeElement element, ExecutableElement method) {
        final String name = getMethodName(method);
        final TypeMirror returnType = method.getReturnType();
        final List<? extends VariableElement> methodParameters = method.getParameters();

        if (!methodParameters.isEmpty()) {
            mProcessingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, "The method " + name + " in " + element.getSimpleName() + " is a getter and therefore is not permitted to have any parameters!", method);
            return;
        }

        if (Utils.isSameType(returnType, void.class)) {
            mProcessingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, "The method " + name + " in " + element.getSimpleName() + " is a getter but it does not return a value! Getters must not have a void return type!");
            return;
        }

        final String key = name.substring(3, name.length());
        final GetterSetterPair pair = getGetterSetterPair(key);
        pair.setGetter(method, returnType);
    }

    private void analyzeIs(TypeElement element, ExecutableElement method) {
        final String name = getMethodName(method);
        final TypeMirror returnType = method.getReturnType();
        final List<? extends VariableElement> methodParameters = method.getParameters();

        if (!Utils.isSameType(returnType, boolean.class)) {
            mProcessingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, "The method " + name + " in " + element.getSimpleName() + " is a getter and starts with 'is'. By convention it may only have a return type of boolean!", method);
            return;
        }

        if (!methodParameters.isEmpty()) {
            mProcessingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, "The method " + name + " in " + element.getSimpleName() + " is a getter and therefore is not permitted to have any parameters!", method);
            return;
        }

        if (Utils.isSameType(returnType, void.class)) {
            mProcessingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, "The method " + name + " in " + element.getSimpleName() + " is a getter but it does not return a value! Getters must not have a void return type!");
            return;
        }

        final String key = name.substring(2, name.length());
        final GetterSetterPair pair = getGetterSetterPair(key);
        pair.setGetter(method, returnType);
    }

    private String getMethodName(ExecutableElement method) {
        return method.getSimpleName().toString();
    }

    private GetterSetterPair getGetterSetterPair(String key) {

        if (!mPairMap.containsKey(key)) {
            final GetterSetterPair pair = new GetterSetterPair();
            pair.setKey(key);
            mPairMap.put(key, pair);
            return pair;
        }

        return mPairMap.get(key);
    }

    private boolean isValidType(TypeMirror mirror) {
        for (Class<?> cls : Constants.CLASSES) {
            if (Utils.isSameType(mirror, cls)) {
                return true;
            }
        }
        return false;
    }
}

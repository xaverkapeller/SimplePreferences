package com.github.wrdlbrnft.preferences;

import com.github.wrdlbrnft.preferences.builder.api.builder.ExecutableBuilder;
import com.github.wrdlbrnft.preferences.builder.api.code.CodeBlock;
import com.github.wrdlbrnft.preferences.builder.api.elements.Constructor;
import com.github.wrdlbrnft.preferences.builder.api.elements.Field;
import com.github.wrdlbrnft.preferences.builder.api.elements.Type;
import com.github.wrdlbrnft.preferences.builder.api.elements.Variable;
import com.github.wrdlbrnft.preferences.builder.impl.ClassBuilder;
import com.github.wrdlbrnft.preferences.builder.impl.Types;
import com.github.wrdlbrnft.preferences.builder.impl.VariableGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.tools.Diagnostic;

public class PreferencesCompiler extends AbstractProcessor {

    private static final Class<?>[] CLASSES = new Class[]{
            int.class, boolean.class, long.class, float.class, String.class, void.class
    };

    private static final String ANNOTATION_DEFAULT_STRING_VALUE = "com.github.wrdlbrnft.simplepreferences.api.DefaultStringValue";
    private static final String ANNOTATION_DEFAULT_BOOLEAN_VALUE = "com.github.wrdlbrnft.simplepreferences.api.DefaultBooleanValue";
    private static final String ANNOTATION_DEFAULT_FLOAT_VALUE = "com.github.wrdlbrnft.simplepreferences.api.DefaultFloatValue";
    private static final String ANNOTATION_DEFAULT_INTEGER_VALUE = "com.github.wrdlbrnft.simplepreferences.api.DefaultIntegerValue";
    private static final String ANNOTATION_DEFAULT_LONG_VALUE = "com.github.wrdlbrnft.simplepreferences.api.DefaultLongValue";
    private static final String ANNOTATION_DEFAULT_RESOURCE_VALUE = "com.github.wrdlbrnft.simplepreferences.api.DefaultResourceValue";

    private static final String[] ANNOTATIONS = new String[]{
            ANNOTATION_DEFAULT_STRING_VALUE,
            ANNOTATION_DEFAULT_BOOLEAN_VALUE,
            ANNOTATION_DEFAULT_FLOAT_VALUE,
            ANNOTATION_DEFAULT_INTEGER_VALUE,
            ANNOTATION_DEFAULT_LONG_VALUE,
            ANNOTATION_DEFAULT_RESOURCE_VALUE
    };

    private static final String[] STRING_BLACKLIST = new String[] {
            ANNOTATION_DEFAULT_BOOLEAN_VALUE,
            ANNOTATION_DEFAULT_FLOAT_VALUE,
            ANNOTATION_DEFAULT_INTEGER_VALUE,
            ANNOTATION_DEFAULT_LONG_VALUE,
            ANNOTATION_DEFAULT_RESOURCE_VALUE
    };

    private static final String[] INTEGER_BLACKLIST = new String[] {
            ANNOTATION_DEFAULT_STRING_VALUE,
            ANNOTATION_DEFAULT_BOOLEAN_VALUE,
            ANNOTATION_DEFAULT_FLOAT_VALUE,
            ANNOTATION_DEFAULT_LONG_VALUE,
            ANNOTATION_DEFAULT_RESOURCE_VALUE
    };

    private static final String[] BOOLEAN_BLACKLIST = new String[] {
            ANNOTATION_DEFAULT_STRING_VALUE,
            ANNOTATION_DEFAULT_FLOAT_VALUE,
            ANNOTATION_DEFAULT_INTEGER_VALUE,
            ANNOTATION_DEFAULT_LONG_VALUE,
            ANNOTATION_DEFAULT_RESOURCE_VALUE
    };

    private static final String[] FLOAT_BLACKLIST = new String[] {
            ANNOTATION_DEFAULT_STRING_VALUE,
            ANNOTATION_DEFAULT_BOOLEAN_VALUE,
            ANNOTATION_DEFAULT_INTEGER_VALUE,
            ANNOTATION_DEFAULT_LONG_VALUE,
            ANNOTATION_DEFAULT_RESOURCE_VALUE
    };

    private static final String[] LONG_BLACKLIST = new String[] {
            ANNOTATION_DEFAULT_STRING_VALUE,
            ANNOTATION_DEFAULT_BOOLEAN_VALUE,
            ANNOTATION_DEFAULT_FLOAT_VALUE,
            ANNOTATION_DEFAULT_INTEGER_VALUE,
            ANNOTATION_DEFAULT_RESOURCE_VALUE
    };

    private static final String[] RESOURCE_BLACKLIST = new String[] {
            ANNOTATION_DEFAULT_STRING_VALUE,
            ANNOTATION_DEFAULT_BOOLEAN_VALUE,
            ANNOTATION_DEFAULT_FLOAT_VALUE,
            ANNOTATION_DEFAULT_INTEGER_VALUE,
            ANNOTATION_DEFAULT_LONG_VALUE
    };

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        if (annotations.size() > 0) {

            final TypeElement annotation = new ArrayList<>(annotations).get(0);
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);

            for (Element element : annotatedElements) {
                if (element instanceof TypeElement) {
                    final TypeElement typeElement = (TypeElement) element;
                    final Type interfaceType = Types.create(typeElement);
                    final String implName = interfaceType.className() + "$$Impl";

                    try {
                        ClassBuilder builder = new ClassBuilder(processingEnv, implName);
                        builder.setPackageName(interfaceType.packageName());
                        builder.setImplements(new HashSet<Type>(Arrays.asList(interfaceType)));
                        builder.setModifiers(EnumSet.of(Modifier.PUBLIC, Modifier.FINAL));

                        final Field fieldContext = builder.addField(Types.Android.CONTEXT, EnumSet.of(Modifier.PRIVATE, Modifier.FINAL));
                        final Field fieldPreferences = builder.addField(Types.Android.SHARED_PREFERENCES, EnumSet.of(Modifier.PRIVATE, Modifier.FINAL));

                        final Constructor constructor = builder.addConstructor(EnumSet.of(Modifier.PUBLIC), new ExecutableBuilder() {

                            private Variable paramPreferences;
                            private Variable paramContext;

                            @Override
                            public List<Variable> createParameterList(VariableGenerator generator) {
                                final List<Variable> parameters = new ArrayList<Variable>();

                                parameters.add(paramContext = generator.generate(Types.Android.CONTEXT));
                                parameters.add(paramPreferences = generator.generate(Types.Android.SHARED_PREFERENCES));

                                return parameters;
                            }

                            @Override
                            public void writeBody(CodeBlock code, VariableGenerator generator) {
                                code.append(fieldPreferences.set(paramPreferences)).append(";\n");
                                code.append(fieldContext.set(paramContext));
                            }
                        });

                        final Map<String, GetterSetterPair> pairMap = new HashMap<>();

                        final List<? extends Element> members = typeElement.getEnclosedElements();
                        for (final Element member : members) {
                            if (member.getKind() == ElementKind.METHOD) {
                                final ExecutableElement method = (ExecutableElement) member;

                                final String name = method.getSimpleName().toString();
                                final Type returnType = Types.create(method.getReturnType());

                                if (name.length() < 4) {
                                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "The method " + name + " in " + interfaceType.className() + "is not a valid getter or setter method.", method);
                                }

                                if (!isValidType(returnType)) {
                                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "The return type of " + name + " in " + interfaceType.className() + " is not a valid. Only int, long, float, boolean, String and void are allowed.", method);
                                }

                                final String operator = name.substring(0, 3);
                                final String key = name.substring(3, name.length());
                                final GetterSetterPair pair;
                                if (!pairMap.containsKey(key)) {
                                    pair = new GetterSetterPair();
                                    pairMap.put(key, pair);
                                } else {
                                    pair = pairMap.get(key);
                                }

                                builder.implementMethod(method, new ExecutableBuilder() {

                                    private Variable parameter;

                                    @Override
                                    public List<Variable> createParameterList(VariableGenerator generator) {
                                        final List<Variable> params = new ArrayList<Variable>();

                                        final List<? extends VariableElement> methodParameters = method.getParameters();
                                        if (methodParameters.size() > 1) {
                                            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "The method " + name + " in " + interfaceType.className() + " has too many parameters! A maximum of 1 parameters is possible.", method);
                                        }

                                        int parameterCount = 0;
                                        for (VariableElement parameter : methodParameters) {
                                            if (parameter.getKind() == ElementKind.PARAMETER) {
                                                final Type parameterType = Types.create(parameter.asType());

                                                if (parameterType == null) {
                                                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Parameter Type is null!", method);
                                                }

                                                if (parameterCount > 0) {
                                                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "The method " + name + " in " + interfaceType.className() + " has too many parameters! A maximum of 1 parameters is possible.", method);
                                                }

                                                if (!isValidType(parameterType)) {
                                                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "The parameter " + parameter.getSimpleName() + " of " + name + " in " + interfaceType.className() + " is not a valid. Only int, long, float, boolean, String and void are allowed.", parameter);
                                                }
                                                params.add(this.parameter = generator.generate(parameterType, parameter.getModifiers()));
                                                parameterCount++;
                                            }
                                        }

                                        return params;
                                    }

                                    @Override
                                    public void writeBody(CodeBlock code, VariableGenerator generator) {
                                        if (operator.equalsIgnoreCase("get")) {
                                            if (returnType == Types.Primitives.VOID) {
                                                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Method " + name + " is a getter but has a return type of void!");
                                            }

                                            if (parameter != null) {
                                                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Method " + name + " is not a valid getter since it also has a parameter!");
                                            }

                                            pair.setGetter(method, returnType);

                                            if (returnType.equals(Types.STRING)) {
                                                code.append("return ").append(fieldPreferences).append(".getString(\"").append(key).append("\", ");
                                                AnnotationMirror mirror;
                                                if ((mirror = getAnnotation(method, ANNOTATION_DEFAULT_STRING_VALUE)) != null) {
                                                    if(hasOneAnnotationOf(method, STRING_BLACKLIST)) {
                                                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Invalid default value annotation found!", method);
                                                    }
                                                    String defaultValue = (String) new ArrayList<>(mirror.getElementValues().values()).get(0).getValue();
                                                    if (defaultValue == null) {
                                                        code.append("null");
                                                    } else {
                                                        code.append("\"").append(defaultValue).append("\"");
                                                    }
                                                } else if ((mirror = getAnnotation(method, ANNOTATION_DEFAULT_RESOURCE_VALUE)) != null) {
                                                    if(hasOneAnnotationOf(method, RESOURCE_BLACKLIST)) {
                                                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Invalid default value annotation found!", method);
                                                    }
                                                    int resId = (Integer) new ArrayList<>(mirror.getElementValues().values()).get(0).getValue();
                                                    code.append(fieldContext).append(".getString(").append(resId).append(")");
                                                } else {
                                                    if(hasOneAnnotationOf(method, STRING_BLACKLIST)) {
                                                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Invalid default value annotation found!", method);
                                                    }
                                                    code.append("null");
                                                }
                                                code.append(")");
                                            } else if (returnType.equals(Types.Primitives.BOOLEAN)) {
                                                if(hasOneAnnotationOf(method, BOOLEAN_BLACKLIST)) {
                                                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Invalid default value annotation found!", method);
                                                }
                                                code.append("return ").append(fieldPreferences).append(".getBoolean(\"").append(key).append("\", ");
                                                boolean defaultValue = false;
                                                AnnotationMirror mirror;
                                                if ((mirror = getAnnotation(method, ANNOTATION_DEFAULT_BOOLEAN_VALUE)) != null) {
                                                    defaultValue = (Boolean) new ArrayList<>(mirror.getElementValues().values()).get(0).getValue();
                                                }
                                                code.append(defaultValue);
                                                code.append(")");
                                            } else if (returnType.equals(Types.Primitives.INTEGER)) {
                                                if(hasOneAnnotationOf(method, INTEGER_BLACKLIST)) {
                                                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Invalid default value annotation found!", method);
                                                }
                                                code.append("return ").append(fieldPreferences).append(".getInt(\"").append(key).append("\", ");
                                                int defaultValue = 0;
                                                AnnotationMirror mirror;
                                                if ((mirror = getAnnotation(method, ANNOTATION_DEFAULT_INTEGER_VALUE)) != null) {
                                                    defaultValue = (Integer) new ArrayList<>(mirror.getElementValues().values()).get(0).getValue();
                                                }
                                                code.append(defaultValue);
                                                code.append(")");
                                            } else if (returnType.equals(Types.Primitives.FLOAT)) {
                                                if(hasOneAnnotationOf(method, FLOAT_BLACKLIST)) {
                                                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Invalid default value annotation found!", method);
                                                }
                                                code.append("return ").append(fieldPreferences).append(".getFloat(\"").append(key).append("\", ");
                                                float defaultValue = 0.0f;
                                                AnnotationMirror mirror;
                                                if ((mirror = getAnnotation(method, ANNOTATION_DEFAULT_FLOAT_VALUE)) != null) {
                                                    defaultValue = (Integer) new ArrayList<>(mirror.getElementValues().values()).get(0).getValue();
                                                }
                                                code.append(defaultValue);
                                                code.append("f)");
                                            } else if (returnType.equals(Types.Primitives.LONG)) {
                                                if(hasOneAnnotationOf(method, LONG_BLACKLIST)) {
                                                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Invalid default value annotation found!", method);
                                                }
                                                code.append("return ").append(fieldPreferences).append(".getLong(\"").append(key).append("\", ");
                                                long defaultValue = 0l;
                                                AnnotationMirror mirror;
                                                if ((mirror = getAnnotation(method, ANNOTATION_DEFAULT_LONG_VALUE)) != null) {
                                                    defaultValue = (Long) new ArrayList<>(mirror.getElementValues().values()).get(0).getValue();
                                                }
                                                code.append(defaultValue);
                                                code.append("l)");
                                            } else {
                                                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Method " + name + " could not be implemented. Return type could not be parsed even though it is of a valid type!");
                                            }

                                        } else if (operator.equalsIgnoreCase("set")) {
                                            if (returnType != Types.Primitives.VOID) {
                                                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Method " + name + " is a setter but it returns a value!");
                                            }

                                            if (parameter == null) {
                                                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Method " + name + " is not a valid setter since it has no parameter!");
                                            }

                                            final Type type = parameter.type();

                                            pair.setSetter(method, type);

                                            if (hasOneAnnotationOf(method, ANNOTATIONS)) {
                                                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Invalid annotation found! You can only annotate getter methods with default value annotations!", method);
                                            }

                                            if (type.equals(Types.STRING)) {
                                                code.append(fieldPreferences).append(".edit().putString(\"").append(key).append("\", ").append(parameter).append(").commit()");
                                            } else if (type.equals(Types.Primitives.BOOLEAN)) {
                                                code.append(fieldPreferences).append(".edit().putBoolean(\"").append(key).append("\", ").append(parameter).append(").commit()");
                                            } else if (type.equals(Types.Primitives.INTEGER)) {
                                                code.append(fieldPreferences).append(".edit().putInt(\"").append(key).append("\", ").append(parameter).append(").commit()");
                                            } else if (type.equals(Types.Primitives.FLOAT)) {
                                                code.append(fieldPreferences).append(".edit().putFloat(\"").append(key).append("\", ").append(parameter).append(").commit()");
                                            } else if (type.equals(Types.Primitives.LONG)) {
                                                code.append(fieldPreferences).append(".edit().putLong(\"").append(key).append("\", ").append(parameter).append(").commit()");
                                            }

                                        } else {
                                            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "The method " + name + " of " + interfaceType.className() + "is not a valid getter or setter method.");
                                        }
                                    }
                                });
                            }
                        }

                        for (GetterSetterPair pair : pairMap.values()) {
                            switch (pair.checkIt()) {

                                case OK:
                                    break;

                                case TYPE_MISMATCH:
                                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "The return type of the getter " + pair.getterElement.getSimpleName() + " does not match the parameter type of " + pair.getterElement.getSimpleName(), pair.getterElement);
                                    break;

                                case NO_GETTER:
                                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "No corresponding getter could be found for " + pair.setterElement.getSimpleName() + "! Check your spelling or add a valid getter!", pair.setterElement);
                                    break;

                                case NO_SETTER:
                                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "No corresponding setter could be found for " + pair.getterElement.getSimpleName() + "! Check your spelling or add a valid setter!", pair.getterElement);
                                    break;
                            }
                        }

                        builder.build();
                    } catch (Exception e) {
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Could not generate implementation of " + interfaceType.fullClassName() + "!");
                        e.printStackTrace();
                    }
                }
            }
        }
        return false;
    }

    private boolean hasOneAnnotationOf(ExecutableElement method, String[] annotations) {
        final List<? extends AnnotationMirror> annotationMirrors = method.getAnnotationMirrors();
        for (AnnotationMirror mirror : annotationMirrors) {
            final DeclaredType declaredType = mirror.getAnnotationType();
            final String annotation = declaredType.toString();
            for (String className : annotations) {
                if (className.equals(annotation)) {
                    return true;
                }
            }
        }
        return false;
    }

    private AnnotationMirror getAnnotation(ExecutableElement method, String annotationClassName) {
        final List<? extends AnnotationMirror> annotationMirrors = method.getAnnotationMirrors();
        for (AnnotationMirror mirror : annotationMirrors) {
            final DeclaredType declaredType = mirror.getAnnotationType();
            final String annotation = declaredType.toString();
            if (annotation.equals(annotationClassName)) {
                return mirror;
            }
        }
        return null;
    }

    private static class GetterSetterPair {

        public static enum Status {
            OK,
            TYPE_MISMATCH,
            NO_GETTER,
            NO_SETTER
        }

        private Type getterType = null;
        private ExecutableElement getterElement;
        private Type setterType = null;
        private ExecutableElement setterElement;

        public void setGetter(ExecutableElement element, Type type) {
            this.getterElement = element;
            this.getterType = type;
        }

        public void setSetter(ExecutableElement element, Type type) {
            this.setterElement = element;
            this.setterType = type;
        }

        public Status checkIt() {
            if (getterType == null) {
                return Status.NO_GETTER;
            }

            if (setterType == null) {
                return Status.NO_SETTER;
            }

            if (!getterType.equals(setterType)) {
                return Status.TYPE_MISMATCH;
            }

            return Status.OK;
        }
    }

    private boolean isValidType(Type type) {
        if (type == Types.Primitives.VOID) {
            return true;
        }

        final String className = type.fullClassName();
        for (Class<?> cls : CLASSES) {
            if (className.equals(cls.getName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        final Set<String> types = new HashSet<>();

        types.add("com.github.wrdlbrnft.simplepreferences.api.Preferences");

        return types;
    }
}

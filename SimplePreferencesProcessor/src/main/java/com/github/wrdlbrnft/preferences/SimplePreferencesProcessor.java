package com.github.wrdlbrnft.preferences;

import com.github.wrdlbrnft.codebuilder.implementations.Implementation;
import com.github.wrdlbrnft.codebuilder.types.Type;
import com.github.wrdlbrnft.preferences.analyzer.PreferencesAnalyzer;
import com.github.wrdlbrnft.preferences.analyzer.PreferencesAnalyzerResult;
import com.github.wrdlbrnft.preferences.builder.ImplementationBuilder;
import com.github.wrdlbrnft.preferences.factory.FactoryBuilder;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;

@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class SimplePreferencesProcessor extends AbstractProcessor {

    private PreferencesAnalyzer mPreferencesAnalyzer;
    private ImplementationBuilder mImplementationBuilder;
    private FactoryBuilder mFactoryBuilder;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {

        mPreferencesAnalyzer = new PreferencesAnalyzer(processingEnv);
        mImplementationBuilder = new ImplementationBuilder(processingEnv);
        mFactoryBuilder = new FactoryBuilder(processingEnv);

        for (TypeElement annotation : annotations) {
            final Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);
            final Set<TypeElement> types = ElementFilter.typesIn(elements);
            for (TypeElement type : types) {
                try {
                    handleType(type);
                } catch (Exception e) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Failed to generate preferences implementation of " + type.getSimpleName() + "!", type);
                    e.printStackTrace(System.out);
                }
            }
        }

        return false;
    }

    private void handleType(TypeElement type) throws IOException {
        final PreferencesAnalyzerResult result = mPreferencesAnalyzer.analyze(type);
        if (result == null) {
            return;
        }

        final Implementation implementation = mImplementationBuilder.build(result);
        mFactoryBuilder.build(result, implementation);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        final Set<String> types = new HashSet<>();

        types.add(SimplePreferencesAnnotations.PREFERENCES);

        return types;
    }
}

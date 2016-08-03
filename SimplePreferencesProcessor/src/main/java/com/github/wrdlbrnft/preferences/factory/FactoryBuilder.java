package com.github.wrdlbrnft.preferences.factory;

import com.github.wrdlbrnft.codebuilder.code.Block;
import com.github.wrdlbrnft.codebuilder.code.SourceFile;
import com.github.wrdlbrnft.codebuilder.elements.values.Values;
import com.github.wrdlbrnft.codebuilder.executables.ExecutableBuilder;
import com.github.wrdlbrnft.codebuilder.executables.Method;
import com.github.wrdlbrnft.codebuilder.executables.Methods;
import com.github.wrdlbrnft.codebuilder.implementations.Implementation;
import com.github.wrdlbrnft.codebuilder.types.Type;
import com.github.wrdlbrnft.codebuilder.types.Types;
import com.github.wrdlbrnft.codebuilder.util.Utils;
import com.github.wrdlbrnft.codebuilder.variables.Variable;
import com.github.wrdlbrnft.codebuilder.variables.Variables;
import com.github.wrdlbrnft.preferences.analyzer.PreferencesAnalyzerResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * Created by kapeller on 13/01/16.
 */
public class FactoryBuilder {

    private static final Method CONTEXT_STUB_GET_SHARED_PREFERENCES = Methods.stub("getSharedPreferences");

    private final ProcessingEnvironment mProcessingEnvironment;

    public FactoryBuilder(ProcessingEnvironment processingEnvironment) {
        mProcessingEnvironment = processingEnvironment;
    }

    public void build(PreferencesAnalyzerResult result, final Type implementationType) throws IOException {
        final TypeElement interfaceElement = result.getInterfaceElement();
        final String sharedPreferencesName = result.getSharedPreferencesName();

        final Implementation.Builder builder = new Implementation.Builder();
        builder.setName(createFactoryName(interfaceElement));
        builder.setModifiers(EnumSet.of(Modifier.PUBLIC, Modifier.FINAL));

        builder.addMethod(new Method.Builder()
                .setModifiers(EnumSet.of(Modifier.PUBLIC, Modifier.STATIC))
                .setName("newInstance")
                .setReturnType(Types.of(interfaceElement))
                .setCode(new ExecutableBuilder() {

                    private Variable mParamContext;

                    @Override
                    protected List<Variable> createParameters() {
                        final List<Variable> parameters = new ArrayList<>();
                        parameters.add(mParamContext = Variables.of(Types.Android.CONTEXT));
                        return parameters;
                    }

                    @Override
                    protected void write(Block block) {
                        final Variable varPreferences = Variables.of(Types.Android.SHARED_PREFERENCES, Modifier.FINAL);
                        block.set(varPreferences, new Block().append(
                                CONTEXT_STUB_GET_SHARED_PREFERENCES.callOnTarget(mParamContext,
                                        Values.of(sharedPreferencesName),
                                        Variables.stub(Types.Android.CONTEXT, "Context.MODE_PRIVATE")
                                )
                        )).append(";").newLine();
                        block.append("return ").append(implementationType.newInstance(mParamContext, varPreferences)).append(";");
                    }
                })
                .build());

        final SourceFile sourceFile = SourceFile.create(mProcessingEnvironment, Utils.getPackageName(interfaceElement));
        sourceFile.write(builder.build());
        sourceFile.flushAndClose();
    }

    private String createFactoryName(TypeElement interfaceElement) {
        return Utils.createGeneratedClassName(interfaceElement, "", "Factory");
    }
}

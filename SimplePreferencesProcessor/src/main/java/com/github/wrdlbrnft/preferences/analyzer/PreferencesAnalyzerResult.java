package com.github.wrdlbrnft.preferences.analyzer;

import java.util.List;

import javax.lang.model.element.TypeElement;

/**
 * Created by kapeller on 10/07/15.
 */
public class PreferencesAnalyzerResult {

    private final TypeElement mInterfaceElement;
    private final List<GetterSetterPair> mPairs;
    private final String mSharedPreferencesName;

    public PreferencesAnalyzerResult(TypeElement interfaceElement, List<GetterSetterPair> pairs, String sharedPreferencesName) {
        mInterfaceElement = interfaceElement;
        mPairs = pairs;
        mSharedPreferencesName = sharedPreferencesName;
    }

    public TypeElement getInterfaceElement() {
        return mInterfaceElement;
    }

    public List<GetterSetterPair> getPairs() {
        return mPairs;
    }

    public String getSharedPreferencesName() {
        return mSharedPreferencesName;
    }
}

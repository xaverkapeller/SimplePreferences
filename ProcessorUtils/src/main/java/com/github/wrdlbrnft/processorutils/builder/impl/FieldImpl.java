package com.github.wrdlbrnft.processorutils.builder.impl;

import java.util.Set;

import javax.lang.model.element.Modifier;

import com.github.wrdlbrnft.processorutils.builder.api.elements.Field;
import com.github.wrdlbrnft.processorutils.builder.api.elements.Type;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 28/12/14
 */
class FieldImpl extends VariableImpl implements Field {

    private String mInitialValue = null;

    public FieldImpl(String name, Type type, Set<Modifier> modifiers) {
        super(name, type, modifiers);
    }

    @Override
    public void setInitialValue(String value) {
        mInitialValue = value;
    }

    public String initialValue() {
        return mInitialValue;
    }
}

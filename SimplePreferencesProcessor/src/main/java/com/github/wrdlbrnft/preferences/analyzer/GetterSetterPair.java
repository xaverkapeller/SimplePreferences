package com.github.wrdlbrnft.preferences.analyzer;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;

/**
 * Created by kapeller on 10/07/15.
 */
public class GetterSetterPair {

    public enum Status {
        OK,
        TYPE_MISMATCH,
        NO_GETTER,
        NO_SETTER
    }

    private String mKey;

    private TypeMirror mGetterType;
    private ExecutableElement mGetterElement;

    private TypeMirror mSetterType;
    private ExecutableElement mSetterElement;

    public void setGetter(ExecutableElement element, TypeMirror type) {
        this.mGetterElement = element;
        this.mGetterType = type;
    }

    public void setSetter(ExecutableElement element, TypeMirror type) {
        this.mSetterElement = element;
        this.mSetterType = type;
    }

    public Status checkIt() {
        if (mGetterType == null) {
            return Status.NO_GETTER;
        }

        if (mSetterType == null) {
            return Status.NO_SETTER;
        }

        if (!mGetterType.equals(mSetterType)) {
            return Status.TYPE_MISMATCH;
        }

        return Status.OK;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        mKey = key;
    }

    public TypeMirror getGetterType() {
        return mGetterType;
    }

    public ExecutableElement getGetterElement() {
        return mGetterElement;
    }

    public TypeMirror getSetterType() {
        return mSetterType;
    }

    public ExecutableElement getSetterElement() {
        return mSetterElement;
    }
}

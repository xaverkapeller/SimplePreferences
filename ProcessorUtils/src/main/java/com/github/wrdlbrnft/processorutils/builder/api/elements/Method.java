package com.github.wrdlbrnft.processorutils.builder.api.elements;

import java.util.Set;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 09/12/14
 */
public interface Method extends Executable {
    public String name();
    public Type returnType();
    public String execute(String target, Variable... parameters);
}

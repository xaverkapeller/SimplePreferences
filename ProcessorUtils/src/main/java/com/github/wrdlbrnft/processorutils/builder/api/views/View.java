package com.github.wrdlbrnft.processorutils.builder.api.views;

import com.github.wrdlbrnft.processorutils.builder.api.code.CodeBlock;
import com.github.wrdlbrnft.processorutils.builder.api.elements.Variable;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 09/12/14
 */
public interface View extends Variable {
    public int id();
    public String setClickListener(CodeBlock onClick);
}

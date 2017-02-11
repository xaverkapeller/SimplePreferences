package com.github.wrdlbrnft.simplepreferences.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 13/01/15
 */
@Target(ElementType.TYPE)
public @interface Preferences {
    String value();
}

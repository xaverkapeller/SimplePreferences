package com.github.wrdlbrnft.simplepreferences;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.reflect.InvocationTargetException;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 13/01/15
 */
public class PreferencesFactory {

    public static <T> T create(Class<T> cls, Context context) {
        final String preferencesName = cls.getName() + "$$Impl__preferences";
        return create(cls, context, preferencesName);
    }

    public static <T> T create(Class<T> cls, Context context, String preferencesName) {
        final SharedPreferences preferences = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        return create(cls, context, preferences);
    }

    public static <T> T create(Class<T> cls, Context context, SharedPreferences preferences) {
        if (!cls.isInterface()) {
            throw new IllegalStateException("The class " + cls + " is not an interface!");
        }

        try {
            final String implName = cls.getName() + "$$Impl";
            final Class<?> implClass = Class.forName(implName);
            return (T) implClass.<SharedPreferences>getConstructor(Context.class, SharedPreferences.class).<T>newInstance(context, preferences);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Implementation of " + cls.getName() + " could not be found!");
        } catch (InstantiationException e) {
            throw new IllegalStateException("Implementation of " + cls.getName() + " could not be instantiated!");
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Implementation of " + cls.getName() + " could not be accessed!");
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Implementation of " + cls.getName() + " seems to be broken!");
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("Constructor of the implementation of " + cls.getName() + " could not be invoked!");
        }
    }
}

package com.bethibande.jsql.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ClassUtils {

    public static <T> T createClassInstance(Class<T> clazz) {
        Constructor<?> con = clazz.getDeclaredConstructors()[0];
        Class<?>[] types = con.getParameterTypes();
        Object[] values = new Object[types.length];

        for (int i = 0; i < types.length; i++) {
            Class<?> type = types[i];

            if(type == byte.class) {
                values[i] = (byte)0;
                continue;
            }
            if(type == short.class) {
                values[i] = (short)0;
                continue;
            }
            if(type == int.class) {
                values[i] = 0;
                continue;
            }
            if(type == long.class) {
                values[i] = (long)0;
                continue;
            }
            if(type == float.class) {
                values[i] = 0f;
                continue;
            }
            if(type == double.class) {
                values[i] = 0d;
                continue;
            }
            if(type == char.class) {
                values[i] = Character.MIN_VALUE;
                continue;
            }
            if(type == boolean.class) {
                values[i] = false;
                continue;
            }

            values[i] = null;
        }

        try {
            return (T) con.newInstance(values);
        } catch(InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

}

package com.bethibande.jsql.fields.adapters;

import com.bethibande.jsql.types.FinalType;

import java.lang.reflect.Field;

public class JavaTranslationParameters {

    private final Class<?> containerClass;
    private final Field field;
    private final Object object;
    private final FinalType targetType;

    public JavaTranslationParameters(Class<?> containerClass, Field field, Object object, FinalType targetType) {
        this.containerClass = containerClass;
        this.field = field;
        this.object = object;
        this.targetType = targetType;
    }

    public Class<?> getContainerClass() {
        return containerClass;
    }

    public Field getField() {
        return field;
    }

    public Object getObject() {
        return object;
    }

    public FinalType getTargetType() {
        return targetType;
    }
}

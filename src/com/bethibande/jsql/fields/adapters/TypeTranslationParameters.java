package com.bethibande.jsql.fields.adapters;

import com.bethibande.jsql.types.SQLType;

import java.lang.reflect.Field;

public class TypeTranslationParameters {

    private final Class<?> containerClass;
    private final Field field;
    private final Class<?> type;
    private final SQLType sqlType;
    private final long sqlSize;

    public TypeTranslationParameters(Class<?> containerClass, Field field, Class<?> type, SQLType sqlType, long sqlSize) {
        this.containerClass = containerClass;
        this.field = field;
        this.type = type;
        this.sqlType = sqlType;
        this.sqlSize = sqlSize;
    }

    public Class<?> getContainerClass() {
        return containerClass;
    }

    public Field getField() {
        return field;
    }

    public Class<?> getType() {
        return type;
    }

    public SQLType getSqlType() {
        return sqlType;
    }

    public long getSqlSize() {
        return sqlSize;
    }
}

package com.bethibande.jsql.examples.abstractclasses;

import com.bethibande.jsql.SQLObject;
import com.bethibande.jsql.fields.SQLField;
import com.bethibande.jsql.types.SQLType;

public abstract class AbstractClass<T> extends SQLObject {

    @SQLField(isKey = true)
    private final String key;
    @SQLField(type = SQLType.VARCHAR, size = 16000)
    private final T value;
    @SQLField
    private final ClassType type;

    public AbstractClass(ClassType type, String key, T value) {
        this.type = type;
        this.key = key;
        this.value = value;
    }

    public ClassType getType() {
        return type;
    }

    public String getKey() {
        return key;
    }

    public T getValue() {
        return value;
    }

    public abstract String getKeyAndValue();

}

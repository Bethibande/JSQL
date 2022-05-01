package com.bethibande.jsql;

import com.bethibande.jsql.fields.SQLFields;

public class SQLFieldInstance<T> {

    private final String fieldName;
    private final T value;
    private final SQLFields.SimpleField fieldData;

    public SQLFieldInstance(String fieldName, T value, SQLFields.SimpleField fieldData) {
        this.fieldName = fieldName;
        this.value = value;
        this.fieldData = fieldData;
    }

    public String getFieldName() {
        return fieldName;
    }

    public SQLFields.SimpleField getFieldData() {
        return fieldData;
    }

    public T getValue() {
        return value;
    }
}

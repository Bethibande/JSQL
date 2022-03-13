package com.bethibande.jsql.fields.adapters;

import com.bethibande.jsql.fields.SQLTypeAdapter;
import com.bethibande.jsql.types.FinalType;
import com.bethibande.jsql.types.SQLType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BooleanAdapter implements SQLTypeAdapter {

    @Override
    public FinalType translateType(Class<?> type, SQLType sqlType, long size) {
        if(type == boolean.class || type == Boolean.class) return FinalType.of(SQLType.BOOL, SQLType.BOOL.getDefaultSize());
        return null;
    }

    @Override
    public <T> T fromSQL(Class<T> type, String name, ResultSet resultSet) throws SQLException {
        if(type == boolean.class || type == Boolean.class) return (T)new Boolean(resultSet.getBoolean(name));
        return null;
    }

    @Override
    public Object toSQL(Object javaObj, FinalType targetType) {
        if(javaObj.getClass() == boolean.class || javaObj.getClass() == Boolean.class) return javaObj;
        return null;
    }

}

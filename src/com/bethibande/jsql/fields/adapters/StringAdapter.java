package com.bethibande.jsql.fields.adapters;

import com.bethibande.jsql.fields.SQLTypeAdapter;
import com.bethibande.jsql.types.FinalType;
import com.bethibande.jsql.types.SQLType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StringAdapter implements SQLTypeAdapter {

    @Override
    public FinalType translateType(Class<?> type, SQLType sqlType, long size) {
        if(type == String.class) return FinalType.of(SQLType.VARCHAR, 64);
        return null;
    }

    @Override
    public <T> T fromSQL(Class<T> type, String name, ResultSet resultSet) throws SQLException {
        if(type == String.class) return (T) resultSet.getString(name);
        return null;
    }

    @Override
    public Object toSQL(Object javaObj, FinalType targetType) {
        if(javaObj.getClass() == String.class) return javaObj;
        return null;
    }
}

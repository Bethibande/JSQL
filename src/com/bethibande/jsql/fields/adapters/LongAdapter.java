package com.bethibande.jsql.fields.adapters;

import com.bethibande.jsql.fields.SQLTypeAdapter;
import com.bethibande.jsql.types.FinalType;
import com.bethibande.jsql.types.SQLType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LongAdapter implements SQLTypeAdapter {

    @Override
    public FinalType translateType(Class<?> type, SQLType sqlType, long size) {
        if(type == long.class || type == Long.class) return FinalType.of(SQLType.BIGINT, SQLType.BIGINT.getDefaultSize());
        return null;
    }

    @Override
    public <T> T fromSQL(Class<T> type, String name, ResultSet resultSet) throws SQLException {
        if(type == long.class || type == Long.class) return (T)new Long(resultSet.getLong(name));
        return null;
    }

    @Override
    public Object toSQL(Object javaObj, FinalType targetType) {
        if(javaObj.getClass() == long.class || javaObj.getClass() == Long.class) return javaObj;
        return null;
    }

}

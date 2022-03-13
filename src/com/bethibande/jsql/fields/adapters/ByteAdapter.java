package com.bethibande.jsql.fields.adapters;

import com.bethibande.jsql.fields.SQLTypeAdapter;
import com.bethibande.jsql.types.FinalType;
import com.bethibande.jsql.types.SQLType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ByteAdapter implements SQLTypeAdapter {

    @Override
    public FinalType translateType(Class<?> type, SQLType sqlType, long size) {
        if(type == byte.class || type == Byte.class) return FinalType.of(SQLType.TINYINT, SQLType.TINYINT.getDefaultSize());
        return null;
    }

    @Override
    public <T> T fromSQL(Class<T> type, String name, ResultSet resultSet) throws SQLException {
        if(type == byte.class || type == Byte.class) return (T)new Byte(resultSet.getByte(name));
        return null;
    }

    @Override
    public Object toSQL(Object javaObj, FinalType targetType) {
        if(javaObj.getClass() == byte.class || javaObj.getClass() == Byte.class) return javaObj;
        return null;
    }

}

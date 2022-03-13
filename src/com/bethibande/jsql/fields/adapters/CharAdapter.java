package com.bethibande.jsql.fields.adapters;

import com.bethibande.jsql.fields.SQLTypeAdapter;
import com.bethibande.jsql.types.FinalType;
import com.bethibande.jsql.types.SQLType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CharAdapter implements SQLTypeAdapter {

    @Override
    public FinalType translateType(Class<?> type, SQLType sqlType, long size) {
        if(type == char.class || type == Character.class) return FinalType.of(SQLType.CHAR, SQLType.CHAR.getDefaultSize());
        return null;
    }

    @Override
    public <T> T fromSQL(Class<T> type, String name, ResultSet resultSet) throws SQLException {
        if(type == char.class || type == Character.class) return (T)new Character(resultSet.getString(name).charAt(0));
        return null;
    }

    @Override
    public Object toSQL(Object javaObj, FinalType targetType) {
        if(javaObj.getClass() == char.class || javaObj.getClass() == Character.class) return javaObj.toString();
        return null;
    }

}

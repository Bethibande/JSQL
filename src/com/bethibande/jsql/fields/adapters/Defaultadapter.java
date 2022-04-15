package com.bethibande.jsql.fields.adapters;

import com.bethibande.jsql.fields.SQLTypeAdapter;
import com.bethibande.jsql.types.FinalType;
import com.bethibande.jsql.types.SQLType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class Defaultadapter implements SQLTypeAdapter {

    @Override
    public FinalType translateType(Class<?> type, SQLType sqlType, long size) {
        if(type == boolean.class || type == Boolean.class) return FinalType.of(SQLType.BOOL, SQLType.BOOL.getDefaultSize());
        if(type == byte.class || type == Byte.class) return FinalType.of(SQLType.TINYINT, SQLType.TINYINT.getDefaultSize());
        if(type == char.class || type == Character.class) return FinalType.of(SQLType.CHAR, SQLType.CHAR.getDefaultSize());
        if(type == double.class || type == Double.class) return FinalType.of(SQLType.DOUBLE, SQLType.DOUBLE.getDefaultSize());
        if(type == float.class || type == Float.class) return FinalType.of(SQLType.FLOAT, SQLType.FLOAT.getDefaultSize());
        if(type == int.class || type == Integer.class) return FinalType.of(SQLType.INT, SQLType.INT.getDefaultSize());
        if(type == long.class || type == Long.class) return FinalType.of(SQLType.BIGINT, SQLType.BIGINT.getDefaultSize());
        if(type == short.class || type == Short.class) return FinalType.of(SQLType.SMALLINT, SQLType.SMALLINT.getDefaultSize());
        if(type == String.class) return FinalType.of(SQLType.VARCHAR, 64);
        if(type == UUID.class) return FinalType.of(SQLType.VARCHAR, 36);
        return null;
    }

    @Override
    public <T> T fromSQL(Class<T> type, String name, ResultSet resultSet) throws SQLException {
        if(type == boolean.class || type == Boolean.class) return (T)new Boolean(resultSet.getBoolean(name));
        if(type == byte.class || type == Byte.class) return (T)new Byte(resultSet.getByte(name));
        if(type == char.class || type == Character.class) return (T)new Character(resultSet.getString(name).charAt(0));
        if(type == double.class || type == Double.class) return (T)new Double(resultSet.getDouble(name));
        if(type == float.class || type == Float.class) return (T)new Float(resultSet.getFloat(name));
        if(type == int.class || type == Integer.class) return (T)new Integer(resultSet.getInt(name));
        if(type == long.class || type == Long.class) return (T)new Long(resultSet.getLong(name));
        if(type == short.class || type == Short.class) return (T)new Short(resultSet.getShort(name));
        if(type == String.class) return (T) resultSet.getString(name);
        if(type == UUID.class) return (T) UUID.fromString(resultSet.getString(name));
        return null;
    }

    @Override
    public Object toSQL(Object javaObj, FinalType targetType) {
        if(javaObj.getClass() == boolean.class || javaObj.getClass() == Boolean.class) return javaObj;
        if(javaObj.getClass() == byte.class || javaObj.getClass() == Byte.class) return javaObj;
        if(javaObj.getClass() == char.class || javaObj.getClass() == Character.class) return javaObj.toString();
        if(javaObj.getClass() == double.class || javaObj.getClass() == Double.class) return javaObj;
        if(javaObj.getClass() == float.class || javaObj.getClass() == Float.class) return javaObj;
        if(javaObj.getClass() == int.class || javaObj.getClass() == Integer.class) return javaObj;
        if(javaObj.getClass() == long.class || javaObj.getClass() == Long.class) return javaObj;
        if(javaObj.getClass() == short.class || javaObj.getClass() == Short.class) return javaObj;
        if(javaObj.getClass() == String.class) return javaObj;
        if(javaObj.getClass() == UUID.class) return javaObj.toString();
        return null;
    }

}

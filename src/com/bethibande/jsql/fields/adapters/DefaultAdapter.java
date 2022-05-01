package com.bethibande.jsql.fields.adapters;

import com.bethibande.jsql.types.FinalType;
import com.bethibande.jsql.types.SQLType;

import java.sql.SQLException;
import java.util.UUID;

public class DefaultAdapter implements SQLTypeAdapter {

    @Override
    public FinalType translateType(TypeTranslationParameters parameters) {
        Class<?> type = parameters.getType();
        SQLType sqlType = parameters.getSqlType();

        if(sqlType != SQLType.AUTO_DETECT) return null;
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
    public Object fromSQL(SQLTranslationParameters parameters) throws SQLException {
        if(parameters.getTargetType() == null || parameters.getAsString() == null) return null;
        Class<?> type = parameters.getTargetType();

        if(type == boolean.class || type == Boolean.class) return parameters.getAsBoolean();
        if(type == byte.class || type == Byte.class) return parameters.getAsByte();
        if(type == char.class || type == Character.class) return parameters.getAsString().charAt(0);
        if(type == double.class || type == Double.class) return parameters.getAsDouble();
        if(type == float.class || type == Float.class) return parameters.getAsFloat();
        if(type == int.class || type == Integer.class) return parameters.getAsInteger();
        if(type == long.class || type == Long.class) return parameters.getAsLong();
        if(type == short.class || type == Short.class) return parameters.getAsShort();
        if(type == String.class) return  parameters.getAsString();
        if(type == UUID.class) return  UUID.fromString(parameters.getAsString());

        return null;
    }

    @Override
    public Object toSQL(JavaTranslationParameters parameters) {
        Object javaObj = parameters.getObject();
        if(javaObj == null) return null;

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

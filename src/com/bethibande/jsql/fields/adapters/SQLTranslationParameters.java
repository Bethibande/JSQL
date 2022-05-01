package com.bethibande.jsql.fields.adapters;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLTranslationParameters {

    private final Class<?> containerClass;
    private final Field field;
    private final Class<?> targetType;
    private final String sqlField;
    private final ResultSet resultSet;

    public SQLTranslationParameters(Class<?> containerClass, Field field, Class<?> targetType, String sqlField, ResultSet resultSet) {
        this.containerClass = containerClass;
        this.field = field;
        this.targetType = targetType;
        this.sqlField = sqlField;
        this.resultSet = resultSet;
    }

    public Class<?> getContainerClass() {
        return containerClass;
    }

    public Field getField() {
        return field;
    }

    public Class<?> getTargetType() {
        return targetType;
    }

    public String getSqlField() {
        return sqlField;
    }

    public ResultSet getResultSet() {
        return resultSet;
    }

    public String getAsString() throws SQLException {
        return resultSet.getString(sqlField);
    }
    public byte getAsByte() throws SQLException {
        return resultSet.getByte(sqlField);
    }
    public short getAsShort() throws SQLException {
        return resultSet.getShort(sqlField);
    }
    public int getAsInteger() throws SQLException {
        return resultSet.getInt(sqlField);
    }
    public long getAsLong() throws SQLException {
        return resultSet.getLong(sqlField);
    }
    public boolean getAsBoolean() throws SQLException {
        return resultSet.getBoolean(sqlField);
    }
    public float getAsFloat() throws SQLException {
        return resultSet.getFloat(sqlField);
    }
    public double getAsDouble() throws SQLException {
        return resultSet.getDouble(sqlField);
    }
}

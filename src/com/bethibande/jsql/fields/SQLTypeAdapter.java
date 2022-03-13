package com.bethibande.jsql.fields;

import com.bethibande.jsql.types.FinalType;
import com.bethibande.jsql.types.SQLType;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface SQLTypeAdapter {

    /**
     * used to determine which mysql type a java type should have, may return null if the adapter has no translation for this type
     * @param type the java type
     * @param sqlType the mysql type specified using the @SQLField annotation
     * @param size the size specified using the @SQLField annotation
     * @return FinalType.of(SQLType, size), may return null
     */
    FinalType translateType(Class<?> type, SQLType sqlType, long size);

    /**
     * Used to load data from mysql tables, use resultSet.get[...](name) to get the value saved in the mysql table
     * @param type the java type of the final Object
     * @param name the name of the mysql/java field
     * @param resultSet the ResultSet of the query
     * @return the final Object
     */
    <T> T fromSQL(Class<T> type, String name, ResultSet resultSet) throws SQLException;

    /**
     * Used to save a java object instance to your myssql table
     * @param javaObj the object to save
     * @param targetType the mysql type you have to format it to, should be the same value that the translateType method returned
     * @return the object to save in your mysql table, has to be something compatible with mysql like String, int [...]
     */
    Object toSQL(Object javaObj, FinalType targetType);

}

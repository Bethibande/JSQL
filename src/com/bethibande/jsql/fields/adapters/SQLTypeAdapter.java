package com.bethibande.jsql.fields.adapters;

import com.bethibande.jsql.types.FinalType;

import java.sql.SQLException;

public interface SQLTypeAdapter {

    /**
     * Used to determine which mysql type a java type should have, may return null if the adapter has no translation for this type
     * @param parameters an Object containing all the info needed for the operation
     * @return FinalType.of(SQLType, size), may return null
     */
    FinalType translateType(TypeTranslationParameters parameters);

    /**
     * Used to load data from mysql tables, use resultSet.get[...](name) to get the value saved in the mysql table
     * @param parameters an Object containing all the info needed for turning sql query results into java Objects
     * @return the final Object
     */
    Object fromSQL(SQLTranslationParameters parameters) throws SQLException;

    /**
     * Used to save a java object instance to your mysql table
     * @param parameters an Object containing all the info necessary in oder to turn a java object into a mysql compatible type
     * @return the object to save in your mysql table, has to be something compatible with mysql like String, int [...]
     */
    Object toSQL(JavaTranslationParameters parameters);

}

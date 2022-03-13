package com.bethibande.jsql.examples.adapters;

import com.bethibande.jsql.SQLTable;
import com.bethibande.jsql.fields.SQLTypeAdapter;
import com.bethibande.jsql.types.FinalType;
import com.bethibande.jsql.types.SQLType;
import com.bethibande.jsql.examples.Person;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonTypeAdapter implements SQLTypeAdapter {

    private SQLTable<Person> table;

    public PersonTypeAdapter(SQLTable<Person> table) {
        this.table = table;
    }

    @Override
    public FinalType translateType(Class<?> type, SQLType sqlType, long size) {
        if(type == Person.class) return FinalType.of(SQLType.VARCHAR, 32);
        return null;
    }

    @Override
    public <T> T fromSQL(Class<T> type, String name, ResultSet resultSet) throws SQLException {
        if(type == Person.class) {
            String person = resultSet.getString(name);
            return (T) table.getItem(person);
        }
        return null;
    }

    @Override
    public Object toSQL(Object javaObj, FinalType targetType) {
        if(javaObj.getClass() == Person.class && targetType.getSqlType() == SQLType.VARCHAR) {
            return ((Person)javaObj).getName();
        }
        return null;
    }
}

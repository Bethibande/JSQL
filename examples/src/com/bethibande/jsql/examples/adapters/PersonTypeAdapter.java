package com.bethibande.jsql.examples.adapters;

import com.bethibande.jsql.SQLTable;
import com.bethibande.jsql.fields.JavaTranslationParameters;
import com.bethibande.jsql.fields.SQLTranslationParameters;
import com.bethibande.jsql.fields.SQLTypeAdapter;
import com.bethibande.jsql.fields.TypeTranslationParameters;
import com.bethibande.jsql.types.FinalType;
import com.bethibande.jsql.types.SQLType;
import com.bethibande.jsql.examples.Person;

import java.sql.SQLException;

public class PersonTypeAdapter implements SQLTypeAdapter {

    private final SQLTable<Person> table;

    public PersonTypeAdapter(SQLTable<Person> table) {
        this.table = table;
    }

    @Override
    public FinalType translateType(TypeTranslationParameters parameters) {
        if(parameters.getType() == Person.class) return FinalType.of(SQLType.VARCHAR, 32);
        return null;
    }

    @Override
    public Object fromSQL(SQLTranslationParameters parameters) throws SQLException {
        Class<?> type = parameters.getTargetType();

        if(type == Person.class) {
            String id = parameters.getAsString();
            return table.getItem(id);
        }
        return null;
    }

    @Override
    public Object toSQL(JavaTranslationParameters parameters) {
        Object javaObj = parameters.getObject();

        if(javaObj.getClass() == Person.class && parameters.getTargetType().getSqlType() == SQLType.VARCHAR) {
            return ((Person)javaObj).getName();
        }
        return null;
    }
}

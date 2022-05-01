package com.bethibande.jsql.examples.abstractclasses;

import com.bethibande.jsql.fields.adapters.JavaTranslationParameters;
import com.bethibande.jsql.fields.adapters.SQLTranslationParameters;
import com.bethibande.jsql.fields.adapters.SQLTypeAdapter;
import com.bethibande.jsql.fields.adapters.TypeTranslationParameters;
import com.bethibande.jsql.types.FinalType;

import java.sql.SQLException;

public class ObjectAdapter implements SQLTypeAdapter {

    @Override
    public FinalType translateType(TypeTranslationParameters parameters) {
        return null;
    }

    @Override
    public Object fromSQL(SQLTranslationParameters parameters) throws SQLException {
        if(parameters.getTargetType() == Object.class && parameters.getContainerClass() == AbstractClass.class) {
            if(parameters.getAsString().matches("[0|1]")) return parameters.getAsString().equals("1");
            return parameters.getAsString();
        }
        return null;
    }

    @Override
    public Object toSQL(JavaTranslationParameters parameters) {
        return null;
    }
}

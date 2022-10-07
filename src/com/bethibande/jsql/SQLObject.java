package com.bethibande.jsql;

import com.bethibande.jsql.fields.SQLFields;
import com.bethibande.jsql.fields.adapters.*;
import com.bethibande.jsql.types.FinalType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public abstract class SQLObject {

    private transient SQLTable owner;

    public void setOwner(SQLTable db) {
        this.owner = db;
    }

    public SQLTable getOwner() {
        return this.owner;
    }

    public void saveField(String field) {
        if(!this.owner.hasField(field)) return;
        this.owner.saveField(this, field);
    }


    public Object serializeField(String fieldName) {
        Object obj = getFieldValue(fieldName);
        SQLFields.SimpleField field = this.owner.getFields().getFields().get(fieldName);

        SQLTypeAdapter ad = new DefaultAdapter();
        FinalType targetType = ad.translateType(
                new TypeTranslationParameters(
                        this.getClass(),
                        field.getField(),
                        field.getField().getType(),
                        field.getType(),
                        field.getTypeSize()
                )
        );

        return ad.toSQL(
                new JavaTranslationParameters(
                        this.getClass(),
                        field.getField(),
                        obj,
                        targetType
                )
        );
    }

    /**
     * @param fieldName
     * @param rs ResultSet containing the field/object you are deserializing
     * @return the deserialized java object
     */
    public Object deserializeField(String fieldName, ResultSet rs) {
        SQLFields.SimpleField field = this.owner.getFields().getFields().get(fieldName);
        if(field == null) throw new RuntimeException("Unknown field in class '" + this.getClass().getName() + ":" + fieldName + "'");

        SQLTypeAdapter ad = new DefaultAdapter();

        try {
            return ad.fromSQL(
                    new SQLTranslationParameters(
                            this.getClass(),
                            field.getField(),
                            field.getField().getType(),
                            field.getName(),
                            rs
                    )
            );
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void save() {
        this.owner.saveItem(this);
    }

    public SQLDataObject<? extends SQLObject> asDataObject() {
        HashMap<String, SQLFieldInstance<?>> fields = new HashMap<>();
        for(SQLFields.SimpleField field : this.owner.getFields().getFields().values()) {
            fields.put(field.getName(), new SQLFieldInstance<>(field.getName(), getFieldValue(field.getName()), field));
        }

        return new SQLDataObject<>(fields, this.owner);
    }

    public void setFieldValue(String fieldName, Object value) {
        SQLFields.SimpleField field = this.owner.getFields().getFields().get(fieldName);
        if(field == null) throw new RuntimeException("Unknown field in class '" + this.getClass().getName() + ":" + fieldName + "'");

        try {
            field.getField().set(this, value);
        } catch(IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public Object getFieldValue(String fieldName) {
        Object obj = null;

        SQLFields.SimpleField sf = this.owner.getFields().getFields().get(fieldName);
        if(sf != null) {
            try {
                obj = sf.getField().get(this);
            } catch(IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return obj;
    }

    public Object getKey() {
        Object obj = null;

        String keyField = this.owner.getFields().getKeyValue();
        try {
            obj = this.owner.getFields().getFields().get(keyField).getField().get(this);
        } catch(IllegalAccessException e) {
            e.printStackTrace();
        }

        return obj;
    }

}

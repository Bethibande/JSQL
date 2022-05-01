package com.bethibande.jsql;

import com.bethibande.jsql.fields.SQLFields;

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

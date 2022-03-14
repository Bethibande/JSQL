package com.bethibande.jsql;

public abstract class SQLObject {

    private transient SQLTable owner;

    public void setOwner(SQLTable db) {
        this.owner = db;
    }

    public SQLTable getOwner() {
        return this.owner;
    }

    public void save() {
        this.owner.saveItem(this);
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

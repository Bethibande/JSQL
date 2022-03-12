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

}

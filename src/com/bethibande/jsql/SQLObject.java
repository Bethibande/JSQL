package com.bethibande.jsql;

public abstract class SQLObject {

    private String table;

    private JSQL owner;

    /*public SQLObject(String table) {
        this.table = table;
    }*/

    public void setOwner(JSQL jsql) {
        this.owner = jsql;
    }

    public JSQL getOwner() {
        return this.owner;
    }

    public String getTable() {
        return this.table;
    }

}

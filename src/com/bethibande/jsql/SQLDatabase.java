package com.bethibande.jsql;

public class SQLDatabase {

    private JSQL owner;
    private String table;

    public SQLDatabase(JSQL owner, String table) {
        this.owner = owner;
        this.table = table;
    }

}

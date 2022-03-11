package com.bethibande.jsql.test;

import com.bethibande.jsql.JSQL;

public class Main {

    public static void main(String[] args) {
        JSQL jsql = new JSQL();
        jsql
                .host("127.0.0.1")
                .user("root", "ph95Mouse")
                .connect();
        jsql.database("test");
    }

}

package com.bethibande.jsql.examples.query;

import com.bethibande.jsql.JSQL;
import com.bethibande.jsql.SQLTable;
import com.bethibande.jsql.examples.Person;
import com.bethibande.jsql.examples.Pet;
import com.google.gson.GsonBuilder;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        JSQL jsql = new JSQL();
        jsql.debug();
        jsql
                .host("127.0.0.1")
                .user("root", "password")
                .connect();
        // select the database, can be done before calling JSQ.connect(); as well
        jsql.database("jsql");

        // register your table, a mysql table will be created if it doesn't exist
        SQLTable<Person> table = jsql.registerTable(Person.class, "test");

        // add another person to the table
        Person test = new Person("test", 54, true, Pet.CAT);
        table.addItem(test);

        System.out.println("Query 1");
        // simple sql 'equals' query
        List<Object> keys = table.query("pet", Pet.CAT);
        for(Object key : keys) {
            Person p = table.getItem(key);
            printObject(p);
        }

        System.out.println("\n\nQuery 2");
        // simple sql 'and' query
        keys = table.queryKeysAnd(new String[]{"pet", "age"}, Pet.CAT, 15);
        for(Object key: keys) {
            Person p = table.getItem(key);
            printObject(p);
        }

        System.out.println("\n\nQuery 3");
        // simple sql 'or' query
        keys = table.queryKeysOr(new String[]{"age", "name"}, 54, "Max");
        for(Object key: keys) {
            Person p = table.getItem(key);
            printObject(p);
        }
    }

    public static void printObject(Object obj) {
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(obj));
    }

}

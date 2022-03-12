package com.bethibande.jsql.examples;

import com.bethibande.jsql.JSQL;
import com.bethibande.jsql.SQLTable;
import com.google.gson.GsonBuilder;

public class Main {

    public static void main(String[] args) {
        JSQL jsql = new JSQL();
        jsql.debug();
        jsql
                .host("127.0.0.1")
                .user("root", "password")
                .connect();
        jsql.database("jsql");

        SQLTable<Person> table = jsql.registerTable(Person.class, "test");

        Person max = new Person("Max", 15, false, Pet.BUNNY);
        table.addItem(max);

        printObject(table.getItem("Max"));

        Person b = table.getItem("Max");
        b.setPet(Pet.CAT);
        b.save();

        printObject(table.getItem("Max"));

        jsql.disconnect();
    }

    public static void printObject(Object obj) {
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(obj));
    }

}

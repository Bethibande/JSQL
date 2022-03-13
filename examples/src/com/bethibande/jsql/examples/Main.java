package com.bethibande.jsql.examples;

import com.bethibande.jsql.JSQL;
import com.bethibande.jsql.SQLTable;
import com.google.gson.GsonBuilder;

public class Main {

    public static void main(String[] args) {
        // create JQSL instance and connect to your mysql server
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

        // create an object and add it to the mysql table
        Person max = new Person("Max", 15, false, Pet.BUNNY);
        table.addItem(max);

        // get an item from the mysql table and print it
        printObject(table.getItem("Max"));

        // get an item from the mysql table, edit it, and save it again
        Person b = table.getItem("Max");
        b.setPet(Pet.CAT);
        b.save();

        // get the item from the mysql table again to prove it was updated
        printObject(table.getItem("Max"));

        // close the mysql connection
        jsql.disconnect();
    }

    public static void printObject(Object obj) {
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(obj));
    }

}

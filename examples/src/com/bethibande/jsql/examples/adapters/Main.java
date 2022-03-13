package com.bethibande.jsql.examples.adapters;

import com.bethibande.jsql.JSQL;
import com.bethibande.jsql.SQLTable;
import com.bethibande.jsql.examples.Person;
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

        SQLTable<Person> people = jsql.registerTable(Person.class, "test");
        // register the type adapter in order to be able to save objects containing an instance of the Person class from the previous example
        jsql.registerTypeAdapter(new PersonTypeAdapter(people)); // !! important, register type adapter before registering the table using it

        // register employee table
        SQLTable<Employee> table = jsql.registerTable(Employee.class, "employees");

        // create employee using the person instance from the previous example, and save it
        Person p = people.getItem("Max");
        Employee employee = new Employee(1, p, "IT");
        table.addItem(employee);

        // load and print the employee instance from the mysql table
        Employee e = table.getItem(1);
        printObject(e);

        jsql.disconnect();
    }

    public static void printObject(Object obj) {
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(obj));
    }

}

package com.bethibande.jsql.examples.abstractclasses;

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
        jsql.registerTypeAdapter(new ObjectAdapter()); // needed since I'm using a generic type in the AbstractClass class which defaults to Object class

        SQLTable<AbstractClass> table = jsql.registerTable(AbstractClass.class, "abstract_test");

        // this will turn the loaded object data into an object of a class which extends/implements the abstract class
        table.setAbstractClassFactory((data) -> {
            ClassType type = data.getEnum(ClassType.class, "type");

            if(type == ClassType.STRING) return new AbstractClassImpl(data.getString("key"), data.getString("value"));
            if(type == ClassType.BOOLEAN) return new AbstractClassBooleanImpl(data.getString("key"), data.getBoolean("value"));
            return null;
        });

        table.add(new AbstractClassImpl("test", "value"));
        table.add(new AbstractClassBooleanImpl("bool", false));

        AbstractClass<?> obj = table.getItem("test");

        System.out.println("key and value: " + obj.getKeyAndValue());
        System.out.println("type " + obj.getClass().getName());

        System.out.println("\n");

        printObject(obj);

        System.out.println("\n");

        obj = table.getItem("bool");

        System.out.println("key and value: " + obj.getKeyAndValue());
        System.out.println("type " + obj.getClass().getName());

        System.out.println("\n");

        printObject(obj);
    }

    public static void printObject(Object obj) {
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(obj));
    }

}

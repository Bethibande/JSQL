package com.bethibande.jsql.examples.cache;

import com.bethibande.jsql.JSQL;
import com.bethibande.jsql.SQLTable;
import com.bethibande.jsql.cache.CacheConfig;
import com.bethibande.jsql.examples.Person;
import com.google.gson.GsonBuilder;

public class Main {

    public static void main(String[] args) {
        // create JSQL instance and connect to your mysql server
        JSQL jsql = new JSQL();
        jsql.debug();
        jsql
                .host("127.0.0.1")
                .user("root", "ph95Mouse")
                .connect();
        // select the database, can be done before calling JSQ.connect(); as well
        jsql.database("jsql");

        // register your table, a mysql table will be created if it doesn't exist
        SQLTable<Person> table = jsql.registerTable(Person.class, "test");

        // !! init cache, print item in console when timeout is reached and item is removed from cache
        table.useCache(new CacheConfig<Person>()
                .size(1) // max 100 objects in cache
                .timeout(5000L) // objects will be removed after not being used to 5 seconds (time in ms)
                .onCacheItemRemove(Main::printObject) // will be executed when an item is being removed from cache
        );
        jsql.startCacheUpdateThread(); // important, call this otherwise, the cache timeout won't work, besides that the cache will work perfectly fine
        // this will create a new thread and call jsql.updateCache() once a second
        // you can also create your own thread/task and call jsql.updateCache() manually

        // Load item so it will be saved in cache
        table.getItem("Max");

        sleep(6500L);

        // close the mysql connection
        jsql.disconnect();
    }

    public static void printObject(Object obj) {
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(obj));
    }

    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

}

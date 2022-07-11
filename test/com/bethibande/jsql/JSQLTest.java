package com.bethibande.jsql;

import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JSQLTest {

    private JSQL jsql = new JSQL();
    private SQLTable<TestPerson> table = null;

    @Test
    @Order(2)
    void startCacheUpdateThread() {
        int count = Thread.activeCount();

        jsql.startCacheUpdateThread();

        assertNotEquals(count, Thread.activeCount());
    }

    /**
     * Add 1000 items to the table
     */
    @Test
    @Order(4)
    void putTest() throws IOException {
        connect();
        registerTable();

        table.deleteAll();

        assertEquals(0, table.countRows());

        for(int i = 0; i < 1000; i++) {
            TestPerson person = new TestPerson("Max-" + i, ThreadLocalRandom.current().nextInt(100));

            table.add(person);
        }

        assertEquals(1000, table.countRows());
    }

    @RepeatedTest(20)
    @Timeout(2)
    @Order(5)
    void getTest() throws IOException {
        connect();
        registerTable();
        putTest();

        for(int i = 0; i < 1000; i++) {
            TestPerson person = table.get("Max-" + i);

            assertEquals("Max-" + i, person.getName());
            assertTrue(person.getAge() >= 0 && person.getAge() < 100);
        }
    }

    @Test
    @Order(3)
    void registerTable() {
        try {
            connect();

            table = jsql.registerTable(TestPerson.class, "junit-test");
        } catch(Throwable e) {
            fail(e);
        }
    }

    @Test
    @Order(6)
    void disconnect() {
        jsql.disconnect();

        assertFalse(jsql.isConnected());
    }

    @Test
    @Order(1)
    void connect() throws IOException {
        jsql
                .host("127.0.0.1")
                .user("root", Files.readString(new File("./password.txt").toPath()))
                .connect();
        jsql.database("jsql");

        jsql.connect();

        assertTrue(jsql.isConnected());
    }
}
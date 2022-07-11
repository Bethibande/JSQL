package com.bethibande.jsql;

import com.bethibande.jsql.fields.SQLField;

public class TestPerson extends SQLObject {

    @SQLField(isKey = true)
    private final String name;
    @SQLField
    private int age;

    public TestPerson(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}

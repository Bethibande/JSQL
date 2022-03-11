package com.bethibande.jsql.test;

import com.bethibande.jsql.SQLField;
import com.bethibande.jsql.SQLObject;

public class TestObj extends SQLObject {

    @SQLField
    private String name;
    @SQLField
    private int age;
    @SQLField
    private Pet pet;

    private String test;

    public TestObj(String name, int age, Pet pet, String test) {
        this.name = name;
        this.age = age;
        this.pet = pet;
        this.test = test;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public Pet getPet() {
        return pet;
    }

    public String getTest() {
        return test;
    }
}

package com.bethibande.jsql.examples;

import com.bethibande.jsql.fields.SQLField;
import com.bethibande.jsql.SQLObject;

// TODO: remove redundant adult parameter in constructor
// TODO: add seed database method?
public class Person extends SQLObject {

    /**
     * !! keys may only be types compatible with mysql like String, int, boolean [...]
     */
    @SQLField(isKey = true)
    private String name;
    @SQLField
    private int age;
    @SQLField
    private boolean adult;
    @SQLField
    private Pet pet;

    public Person(String name, int age, boolean adult, Pet pet) {
        this.name = name;
        this.age = age;
        this.adult = adult;
        this.pet = pet;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public boolean isAdult() {
        return adult;
    }

    public Pet getPet() {
        return pet;
    }
}

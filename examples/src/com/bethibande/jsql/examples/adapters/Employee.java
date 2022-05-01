package com.bethibande.jsql.examples.adapters;

import com.bethibande.jsql.fields.SQLField;
import com.bethibande.jsql.SQLObject;
import com.bethibande.jsql.examples.Person;

public class Employee extends SQLObject {

    @SQLField(isKey = true)
    private int workerId;
    @SQLField
    private Person person;
    @SQLField
    private String department;

    public Employee(int workerId, Person person, String department) {
        this.workerId = workerId;
        this.person = person;
        this.department = department;
    }

    public int getWorkerId() {
        return workerId;
    }

    public Person getPerson() {
        return person;
    }

    public String getDepartment() {
        return department;
    }

    public void setWorkerId(int workerId) {
        this.workerId = workerId;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}

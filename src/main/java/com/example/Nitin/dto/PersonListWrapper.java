package com.example.Nitin.dto;

import com.example.Nitin.entity.Person;
import java.util.ArrayList;
import java.util.List;

public class PersonListWrapper {
    private List<Person> people = new ArrayList<>();

    public List<Person> getPeople() {
        return people;
    }

    public void setPeople(List<Person> people) {
        this.people = people;
    }
}

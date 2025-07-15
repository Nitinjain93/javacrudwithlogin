package com.example.Nitin.service;

import com.example.Nitin.entity.Person;
import com.example.Nitin.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonService {
    @Autowired
    private PersonRepository personRepository;

    public List<Person> getAll() {
        return personRepository.findAll();
    }

    public Person save(Person person) {
        return personRepository.save(person);
    }

    public void delete(Long id) {
        personRepository.deleteById(id);
    }

    public Object get(Long id) {
        return personRepository.findById(id).orElse(null);
    }
}

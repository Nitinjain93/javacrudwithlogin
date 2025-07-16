package com.example.Nitin.service;

import com.example.Nitin.entity.Person;
import com.example.Nitin.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.io.*;

@Service
public class PersonService {
    @Autowired
    private PersonRepository personRepository;

    public List<Person> getAll() {
        return personRepository.findAll();
    }

    public Page<Person> getAllPaginated(int page, int size) {
        return personRepository.findAll(PageRequest.of(page, size));
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

    public void saveFromCSV(MultipartFile file) throws IOException {
        List<Person> people = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
        String line;

        boolean isFirst = true;
        while ((line = reader.readLine()) != null) {
            if (isFirst) { isFirst = false; continue; } // skip header
            String[] fields = line.split(",");
            if (fields.length >= 4) {
                Person person = new Person();
                person.setName(fields[0].trim());
                person.setEmail(fields[1].trim());
                person.setMobile(fields[2].trim());
                person.setPan(fields[3].trim());
                people.add(person);
            }
        }

        personRepository.saveAll(people);
    }
}

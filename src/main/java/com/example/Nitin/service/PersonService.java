package com.example.Nitin.service;

import com.example.Nitin.entity.Person;
import com.example.Nitin.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final Validator validator;

    @Autowired
    public PersonService(PersonRepository personRepository, Validator validator) {
        this.personRepository = personRepository;
        this.validator = validator;
    }

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

    public Person get(Long id) {
        return personRepository.findById(id).orElse(null);
    }

    public void saveAll(List<Person> people) {
        personRepository.saveAll(people);
    }

    // ---------- CSV Upload Support Classes ----------
    public static class CSVError {
        private final int rowNum;
        private final String[] rowData;
        private final String error;

        public CSVError(int rowNum, String[] rowData, String error) {
            this.rowNum = rowNum;
            this.rowData = rowData;
            this.error = error;
        }

        public int getRowNum() {
            return rowNum;
        }

        public String[] getRowData() {
            return rowData;
        }

        public String getError() {
            return error;
        }
    }

    public static class CSVUploadResult {
        public final int successCount;
        public final int failureCount;
        public final List<Person> validData;
        public final List<CSVError> errors;

        public CSVUploadResult(int successCount, int failureCount, List<Person> validData, List<CSVError> errors) {
            this.successCount = successCount;
            this.failureCount = failureCount;
            this.validData = validData;
            this.errors = errors;
        }
    }

    // ---------- CSV Preview Logic ----------
    public CSVUploadResult previewCSV(MultipartFile file) throws IOException {
        List<Person> validPersons = new ArrayList<>();
        List<CSVError> errors = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            int rowNum = 0;
            boolean isFirst = true;

            while ((line = reader.readLine()) != null) {
                rowNum++;
                if (isFirst) {
                    isFirst = false;
                    continue;
                }

                if (line.trim().isEmpty()) continue;

                String[] fields = line.split(",", -1); // keep empty fields
                if (fields.length < 4) {
                    errors.add(new CSVError(rowNum, fields, "Missing fields"));
                    continue;
                }

                Person person = new Person();
                person.setName(fields[0].trim());
                person.setEmail(fields[1].trim());
                person.setMobile(fields[2].trim());
                person.setPan(fields[3].trim());

                Set<ConstraintViolation<Person>> violations = validator.validate(person);
                if (violations.isEmpty()) {
                    validPersons.add(person);
                } else {
                    String errMsg = violations.stream()
                            .map(ConstraintViolation::getMessage)
                            .collect(Collectors.joining("; "));
                    errors.add(new CSVError(rowNum, fields, errMsg));
                }
            }
        }

        return new CSVUploadResult(validPersons.size(), errors.size(), validPersons, errors);
    }
}

package com.example.Nitin.controller;

import com.example.Nitin.entity.Person;
import com.example.Nitin.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.Resource;


@Controller
public class PersonController {

    @Autowired
    private PersonService personService;

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("people", personService.getAll());
        return "list";
    }

    @GetMapping("/")
    public String listPeople(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<Person> peoplePage = personService.getAllPaginated(page, 5);  // 5 per page
        model.addAttribute("people", peoplePage);
        return "list";
    }

    @GetMapping("/add")
    public String form(Model model) {
        model.addAttribute("person", new Person());
        return "form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("person") Person person,
                       BindingResult result,
                       Model model) {
        if (result.hasErrors()) {
            return "form";  // this should be the name of your Thymeleaf form view (e.g., form.html)
        }

        personService.save(person);
        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("person", personService.get(id));
        return "form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        personService.delete(id);
        return "redirect:/";
    }

    @PostMapping("/upload")
    public String uploadCSV(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            personService.saveFromCSV(file);
            redirectAttributes.addFlashAttribute("message", "CSV uploaded successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Error: " + e.getMessage());
        }
        return "redirect:/upload";
    }

    @GetMapping("/upload")
    public String uploadPage() {
        return "upload";
    }

    @GetMapping("/sample-csv")
    public ResponseEntity<Resource> downloadSampleCSV() {
        String content = "name,email,mobile,pan\n" +
                "John Doe,john@example.com,9876543210,ABCDE1234F\n" +
                "Jane Smith,jane@example.com,9123456789,XYZAB1234K\n";

        ByteArrayResource resource = new ByteArrayResource(content.getBytes());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sample_person.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }

}


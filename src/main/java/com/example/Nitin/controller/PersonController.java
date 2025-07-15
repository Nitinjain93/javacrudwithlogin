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

import javax.validation.Valid;

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
}


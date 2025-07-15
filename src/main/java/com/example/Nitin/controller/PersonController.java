package com.example.Nitin.controller;

import com.example.Nitin.entity.Person;
import com.example.Nitin.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PersonController {

    @Autowired
    private PersonService personService;

    @GetMapping("/")
    public String list(Model model) {
        model.addAttribute("people", personService.getAll());
        return "list";
    }

    @GetMapping("/add")
    public String form(Model model) {
        model.addAttribute("person", new Person());
        return "form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Person person) {
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


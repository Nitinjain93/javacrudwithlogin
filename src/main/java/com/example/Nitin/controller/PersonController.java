package com.example.Nitin.controller;

import com.example.Nitin.dto.PersonListWrapper;
import com.example.Nitin.entity.Person;
import com.example.Nitin.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Controller
public class PersonController {

    @Autowired
    private PersonService personService;

    // List all people (paginated)
    @GetMapping("/")
    public String listPeople(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<Person> peoplePage = personService.getAllPaginated(page, 10);
        model.addAttribute("people", peoplePage);
        return "list";
    }

    // Full list without pagination (optional)
    @GetMapping("/list")
    public String listAll(Model model) {
        model.addAttribute("people", personService.getAll());
        return "list";
    }

    // Show form for new person
    @GetMapping("/add")
    public String showForm(Model model) {
        model.addAttribute("person", new Person());
        return "form";
    }

    // Save new or edited person
    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("person") Person person,
                       BindingResult result, Model model, RedirectAttributes attr) {
        if (result.hasErrors()) {
            return "form";
        }
        personService.save(person);
        attr.addFlashAttribute("toastMessage", "Data saved successfully!");
        return "redirect:/";
    }

    // Edit existing person
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("person", personService.get(id));
        return "form";
    }

    // Delete person
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        personService.delete(id);
        return "redirect:/";
    }

    // CSV upload page
    @GetMapping("/upload")
    public String showUploadPage() {
        return "upload";
    }

    // Preview uploaded CSV file
    @PostMapping("/upload-preview")
    public String previewCSV(@RequestParam("file") MultipartFile file, Model model) throws IOException {
        PersonService.CSVUploadResult result = personService.previewCSV(file);

        PersonListWrapper wrapper = new PersonListWrapper();
        wrapper.setPeople(result.validData);

        model.addAttribute("wrapper", wrapper);
        model.addAttribute("errors", result.errors);
        model.addAttribute("successCount", result.successCount);
        model.addAttribute("failureCount", result.failureCount);
        model.addAttribute("errorCSV", buildErrorCSV(result.errors));

        return "upload";
    }

    // Save only valid previewed data
    @PostMapping("/upload-save")
    public String saveCSV(@ModelAttribute PersonListWrapper wrapper, RedirectAttributes attr) {
        personService.saveAll(wrapper.getPeople());
        attr.addFlashAttribute("toastMessage", "Data saved successfully!");
        return "redirect:/";
    }

    // Download sample CSV file
    @GetMapping("/sample-csv")
    public ResponseEntity<Resource> downloadSampleCSV() {
        String content = "name,email,mobile,pan\n" +
                "John Doe,john@example.com,9876543210,ABCDE1234F\n" +
                "Jane Smith,jane@example.com,9123456789,XYZAB1234K\n";

        ByteArrayResource resource = new ByteArrayResource(content.getBytes());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sample_person.csv")
                .contentType(MediaType.TEXT_PLAIN)
                .body(resource);
    }

    // Download failed rows CSV file
    @GetMapping("/download-errors")
    public void downloadErrors(@RequestParam("errors") String encodedData,
                               HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=failed_rows.csv");

        String decoded = URLDecoder.decode(encodedData, StandardCharsets.UTF_8.name());
        String[] rows = decoded.split("\n");

        PrintWriter writer = response.getWriter();
        writer.println("name,email,mobile,pan,error");
        for (String row : rows) {
            writer.println(row);
        }
        writer.flush();
    }

    private String buildErrorCSV(List<PersonService.CSVError> errors) {
        StringBuilder errorCsvBuilder = new StringBuilder();
        for (PersonService.CSVError e : errors) {
            String[] row = Optional.ofNullable(e.getRowData()).orElse(new String[]{"", "", "", ""});
            String error = Optional.ofNullable(e.getError()).orElse("Unknown error");

            errorCsvBuilder.append(String.join(",", row)).append(",").append(error).append("\n");
        }
        return errorCsvBuilder.toString();
    }

}

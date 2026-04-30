package io.github.ren1208.digital_library.controllers;

import io.github.ren1208.digital_library.models.Person;
import io.github.ren1208.digital_library.security.PersonDetails;
import io.github.ren1208.digital_library.services.PeopleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Artyom Semenchenko
 */

@Controller
@RequestMapping()
public class AdminController {

    private final PeopleService peopleService;

    public AdminController(PeopleService peopleService) {
        this.peopleService = peopleService;
    }

    @GetMapping("/setAdmin")
    public String showSetAdminPage(Model model) {
        List<Person> users = peopleService.findAll().stream()
                .filter(p -> "ROLE_USER".equals(p.getRole()))
                .collect(Collectors.toList());
        model.addAttribute("people", users);
        return "/setAdmin";
    }

    @PostMapping("/setAdmin")
    public String setAdmin(@RequestParam("personId") int personId) {
        Person existingPerson = peopleService.findOne(personId);
        existingPerson.setRole("ROLE_ADMIN");
        peopleService.save(existingPerson);
        return "redirect:/hello";
    }

    @GetMapping("/removeAdmin")
    public String showRemoveAdminPage(Model model) {
        List<Person> users = peopleService.findAll().stream()
                .filter(p -> "ROLE_ADMIN".equals(p.getRole()))
                .collect(Collectors.toList());
        model.addAttribute("people", users);
        return "/removeAdmin";
    }

    @PostMapping("/removeAdmin")
    public String removeAdmin(@RequestParam("personId") int personId) {
        Person existingPerson = peopleService.findOne(personId);
        existingPerson.setRole("ROLE_USER");
        peopleService.save(existingPerson);
        return "redirect:/hello";
    }

}

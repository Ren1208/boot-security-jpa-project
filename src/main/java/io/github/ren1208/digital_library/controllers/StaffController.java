package io.github.ren1208.digital_library.controllers;

import io.github.ren1208.digital_library.models.Person;
import io.github.ren1208.digital_library.security.PersonDetails;
import io.github.ren1208.digital_library.services.PeopleService;
import io.github.ren1208.digital_library.util.PersonValidator;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/staff")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class StaffController {

    private final PeopleService peopleService;
    private final PasswordEncoder passwordEncoder;
    private final PersonValidator personValidator;

    public StaffController(PeopleService peopleService, PasswordEncoder passwordEncoder, PersonValidator personValidator) {
        this.peopleService = peopleService;
        this.passwordEncoder = passwordEncoder;
        this.personValidator = personValidator;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("people", peopleService.findAll());
        return "staff/index";
    }

    @GetMapping("/new")
    public String newStaffPage(@ModelAttribute("person") Person person) {
        return "staff/new";
    }

    @PostMapping
    public String create(@ModelAttribute("person") @Valid Person person,
                         BindingResult bindingResult,
                         @RequestParam("tempPassword") String tempPassword,
                         @RequestParam(value = "role", required = false) String role,
                         Authentication auth) {

        personValidator.validate(person, bindingResult);

        if (tempPassword == null || tempPassword.trim().length() < 6) {
            bindingResult.reject("tempPassword", "Временный пароль должен содержать минимум 6 символов");
        }

        if (bindingResult.hasErrors()) {
            return "staff/new";
        }

        PersonDetails current = (PersonDetails) auth.getPrincipal();
        String currentRole = current.person().getRole();

        String targetRole;
        if ("ROLE_SUPER_ADMIN".equals(currentRole) && "ROLE_ADMIN".equals(role)) {
            targetRole = "ROLE_ADMIN";
        } else {
            targetRole = "ROLE_LIBRARIAN";
        }

        person.setPassword(passwordEncoder.encode(tempPassword));
        person.setRole(targetRole);
        person.setMustChangePassword(true);
        person.setActive(true);
        peopleService.save(person);

        return "redirect:/staff";
    }

    @PostMapping("/{id}/resetPassword")
    public String resetPassword(@PathVariable int id,
                                @RequestParam("tempPassword") String tempPassword, Authentication auth) {
        if (tempPassword == null || tempPassword.trim().length() < 6) {
            return "redirect:/staff?passwordError";
        }
        Person person = peopleService.findOne(id);
        PersonDetails current = (PersonDetails) auth.getPrincipal();
        if (person == null || "ROLE_SUPER_ADMIN".equals(person.getRole()) || current.person().getPersonId() == person.getPersonId()) {
            return "redirect:/staff?forbidden";
        }
        person.setPassword(passwordEncoder.encode(tempPassword));
        person.setMustChangePassword(true);
        peopleService.save(person);
        return "redirect:/staff";
    }

    @PostMapping("/{id}/toggleActive")
    public String toggleActive(@PathVariable int id, Authentication auth) {
        Person person = peopleService.findOne(id);
        PersonDetails current = (PersonDetails) auth.getPrincipal();
        if (person == null || "ROLE_SUPER_ADMIN".equals(person.getRole()) || current.person().getPersonId() == person.getPersonId()) {
            return "redirect:/staff?forbidden";
        }
        person.setActive(!person.isActive());
        peopleService.save(person);
        return "redirect:/staff";
    }
}

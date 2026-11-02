package io.github.ren1208.digital_library.controllers;

import io.github.ren1208.digital_library.models.Person;
import io.github.ren1208.digital_library.security.PersonDetails;
import io.github.ren1208.digital_library.services.PeopleService;
import io.github.ren1208.digital_library.util.PersonValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * @author Artyom Semenchenko
 */

@Controller
@RequestMapping("/people")
public class PeopleController {

    private final PeopleService peopleService;
    private final PersonValidator personValidator;

    public PeopleController(PeopleService peopleService, PersonValidator personValidator) {
        this.peopleService = peopleService;
        this.personValidator = personValidator;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public String index(Model model) {
        model.addAttribute("people", peopleService.findAll());
        return "people/index";
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'LIBRARIAN')")
    public String show(@PathVariable int id, Model model) {
        Person person = peopleService.findOne(id);

        if (person == null) {
            return "redirect:/people";
        }

        model.addAttribute("person", person);
        model.addAttribute("books", peopleService.getBooksByPersonId(id));
        return "people/show";
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public String edit(Model model, @PathVariable int id) {
        Person person = peopleService.findOne(id);

        if (person == null) {
            return "redirect:/people";
        }

        model.addAttribute("person", person);
        return "people/edit";
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public String update(@ModelAttribute("person") @Valid Person person,
                         BindingResult bindingResult,
                         @PathVariable int id) {

        person.setPersonId(id);

        personValidator.validate(person, bindingResult);

        if (bindingResult.hasErrors()) {
            return "people/edit";
        }

        peopleService.update(id, person);
        return "redirect:/people/{id}";
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public String delete(@PathVariable int id, Authentication authentication) {

        Person person = peopleService.findOne(id);

        if (person == null) {
            return "redirect:/people";
        }

        PersonDetails current = (PersonDetails) authentication.getPrincipal();
        Person currentPerson = current.person();
        String currentRole = currentPerson.getRole();

        if ("ROLE_SUPER_ADMIN".equals(person.getRole())) {
            return "redirect:/people?forbidden";
        }

        if ("ROLE_ADMIN".equals(person.getRole())
                && !"ROLE_SUPER_ADMIN".equals(currentRole)) {
            return "redirect:/people?forbidden";
        }

        if (person.getPersonId() == currentPerson.getPersonId()) {
            return "redirect:/people?forbidden";
        }

        peopleService.delete(id);
        return "redirect:/people";
    }
}

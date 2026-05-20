package io.github.ren1208.digital_library.controllers;

import io.github.ren1208.digital_library.models.Person;
import io.github.ren1208.digital_library.security.PersonDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Artyom Semenchenko
 */

@Controller
public class HelloController {

    @GetMapping("/hello")
    public String sayHello(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (isAuthenticated(authentication)) {
            PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
            Person person = personDetails.getPerson();
            model.addAttribute("person", person);
        }

        return "hello";
    }

    @GetMapping("/showUserInfo")
    public String showUserInfo(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (isAuthenticated(authentication)) {
            PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
            Person person = personDetails.getPerson();
            System.out.println("Person ID: " + person.getPersonId());
            System.out.println("Username: " + person.getName());
            System.out.println("Role: " + person.getRole());
            System.out.println("Books count: " + person.getBooks().size());
            model.addAttribute("person", person);
        }

        return "hello";
    }

    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof String)
                && authentication.getPrincipal() instanceof PersonDetails;
    }

}

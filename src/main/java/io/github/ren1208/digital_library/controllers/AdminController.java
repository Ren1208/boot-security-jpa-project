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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Artyom Semenchenko
 */

@Controller
@RequestMapping()
public class AdminController {

    private final PeopleService peopleService;

    @Autowired
    public AdminController(PeopleService peopleService) {
        this.peopleService = peopleService;
    }

    @GetMapping("/become-admin")
    public String showBecomeAdminPage() {
        return "/become-admin";
    }

    @PostMapping("/became-admin")
        public String becomeAdmin(@ModelAttribute("secretPassword") String secretPassword,
                                  Model model) {
            if (secretPassword.equals("admin123")) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (isAuthenticated(authentication)) {
                    PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
                    Person person = personDetails.getPerson();

                    peopleService.updateUserRole(person, "ROLE_ADMIN");

                    PersonDetails updatedPersonDetails = new PersonDetails(person);

                    SecurityContextHolder.getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken(
                                    updatedPersonDetails,
                                    authentication.getCredentials(),
                                    updatedPersonDetails.getAuthorities()
                            )
                    );
                }
                return "redirect:/hello";
            }

        else {
            model.addAttribute("error", "Неверный секретный пароль");
            return "/become-admin";
        }
    }

    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof String)
                && authentication.getPrincipal() instanceof PersonDetails;
    }
}

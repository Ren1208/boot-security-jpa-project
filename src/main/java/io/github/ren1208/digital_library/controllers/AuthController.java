package io.github.ren1208.digital_library.controllers;

import io.github.ren1208.digital_library.DTO.ChangePasswordForm;
import io.github.ren1208.digital_library.models.Person;
import io.github.ren1208.digital_library.security.PersonDetails;
import io.github.ren1208.digital_library.services.PeopleService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Artyom Semenchenko
 */

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final PeopleService peopleService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(PeopleService peopleService, PasswordEncoder passwordEncoder) {
        this.peopleService = peopleService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/change-password")
    public String changePasswordPage(@ModelAttribute("form") ChangePasswordForm form,
                                     Authentication authentication) {

        Person person = extractPerson(authentication);

        if (person == null || !person.isMustChangePassword()) {
            return "redirect:/hello";
        }

        return "auth/change-password";
    }

    @PostMapping("/change-password")
    public String performChangePassword(@ModelAttribute("form") @Valid ChangePasswordForm form,
                                        BindingResult bindingResult,
                                        Authentication authentication) {

        PersonDetails details = (PersonDetails) authentication.getPrincipal();
        Person person = details.person();

        if (!person.isMustChangePassword()) {
            return "redirect:/hello";
        }

        if (bindingResult.hasErrors()) {
            return "auth/change-password";
        }

        if (!form.getNewPassword().equals(form.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "", "Пароли не совпадают");
            return "auth/change-password";
        }

        if (passwordEncoder.matches(form.getNewPassword(), person.getPassword())) {
            bindingResult.rejectValue("newPassword", "",
                    "Новый пароль не должен совпадать с временным");
            return "auth/change-password";
        }

        person.setPassword(passwordEncoder.encode(form.getNewPassword()));
        person.setMustChangePassword(false);
        peopleService.save(person);

        SecurityContextHolder.clearContext();

        return "redirect:/auth/login?passwordChanged";
    }

    private Person extractPerson(Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof PersonDetails(Person person))) {
            return null;
        }
        return person;
    }
}
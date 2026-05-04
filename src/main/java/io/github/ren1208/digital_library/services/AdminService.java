package io.github.ren1208.digital_library.services;

import io.github.ren1208.digital_library.models.Person;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AdminService {

    private  final PeopleService peopleService;


    public AdminService(PeopleService peopleService) {
        this.peopleService = peopleService;
    }

    public List<Person> getAllRegularUsers() {
        return peopleService.findAll().stream()
                .filter(p -> "ROLE_USER".equals(p.getRole()))
                .collect(Collectors.toList());
    }

    public List<Person> getAllAdmins() {
        return peopleService.findAll().stream()
                .filter(p -> "ROLE_ADMIN".equals(p.getRole()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void grantAdminRole(int personId) {
        peopleService.updateUserRole(peopleService.findOne(personId), "ROLE_ADMIN");
    }

    @Transactional
    public void revokeAdminRole(int personId) {
        peopleService.updateUserRole(peopleService.findOne(personId), "ROLE_USER");
    }

}

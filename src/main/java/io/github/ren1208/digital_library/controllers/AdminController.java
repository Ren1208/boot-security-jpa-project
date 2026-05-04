package io.github.ren1208.digital_library.controllers;

import io.github.ren1208.digital_library.services.AdminService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * @author Artyom Semenchenko
 */

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/setAdmin")
    public String showSetAdminPage(Model model) {
        model.addAttribute("people", adminService.getAllRegularUsers());
        return "/admin/setAdmin";
    }

    @PostMapping("/setAdmin")
    public String setAdmin(@RequestParam("personId") int personId) {
        adminService.grantAdminRole(personId);
        return "redirect:/hello";
    }

    @GetMapping("/removeAdmin")
    public String showRemoveAdminPage(Model model) {
        model.addAttribute("people", adminService.getAllAdmins());
        return "/admin/removeAdmin";
    }

    @PostMapping("/removeAdmin")
    public String removeAdmin(@RequestParam("personId") int personId) {
        adminService.revokeAdminRole(personId);
        return "redirect:/hello";
    }

}

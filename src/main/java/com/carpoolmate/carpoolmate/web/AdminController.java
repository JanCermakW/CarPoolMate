package com.carpoolmate.carpoolmate.web;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import com.carpoolmate.carpoolmate.model.User;
import com.carpoolmate.carpoolmate.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    // Display admin page with pending drivers
    @GetMapping("/drivers")
    public String showPendingDrivers(Model model) {
        List<User> pendingDrivers = userService.getPendingDrivers();
        model.addAttribute("pendingDrivers", pendingDrivers);
        return "admin/drivers";
    }

    // Approve a driver by ID
    @PostMapping("/drivers/approve/{id}")
    public String approveDriver(@PathVariable Long id) {
        userService.approveDriver(id);
        return "redirect:/admin/drivers?success";
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.getUserByEmail(currentUserEmail);
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("currentUser", currentUser);

        return "users";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return "redirect:/admin/users?success";
    }

}

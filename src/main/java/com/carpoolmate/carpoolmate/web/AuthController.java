package com.carpoolmate.carpoolmate.web;

import com.carpoolmate.carpoolmate.model.User;
import com.carpoolmate.carpoolmate.service.FileStorageService;
import com.carpoolmate.carpoolmate.service.MailService;
import com.carpoolmate.carpoolmate.service.UserService;
import com.carpoolmate.carpoolmate.web.dto.UserRegistrationDto;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/registration")
public class AuthController {

    private UserService userService;

    public AuthController(UserService userService) {
        super();
        this.userService = userService;
    }

    @Autowired
    private MailService mailService;

    @Autowired
    private FileStorageService fileStorageService;

    @ModelAttribute("user")
    public UserRegistrationDto userRegistrationDto() {
        return new UserRegistrationDto();
    }

    @GetMapping
    public String showRegistrationForm() {
        return "registration";
    }

    @PostMapping
    public String registerUserAccount(
            @ModelAttribute("user") UserRegistrationDto registrationDto,
            @RequestParam(value = "registerAsDriver", defaultValue = "false") boolean registerAsDriver,
            @RequestParam(required = false) MultipartFile driverImage,
            HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {

        // Kontrola: uživatel již existuje
        if (userService.getUserByEmail(registrationDto.getEmail()) != null) {
            return "redirect:/registration?error";
        }

        // Kontrola hesla
        if (!userService.validatePassword(registrationDto.getPassword())) {
            return "redirect:/registration?errorPasswd";
        }

        // Rozhodnutí: Registrace běžného uživatele nebo řidiče
        if (registerAsDriver) {
            // Logika pro registraci řidiče
            if (driverImage != null && !driverImage.isEmpty()) {
                String imagePath = fileStorageService.storeFile(driverImage);
                registrationDto.setDriverImagePath("/img/" + imagePath);
            }
            if (userService.saveDriver(registrationDto) == null) {
                return "redirect:/registration?errorGeneral";
            }
        } else {
            // Logika pro registraci běžného uživatele
            if (userService.save(registrationDto) == null) {
                return "redirect:/registration?errorGeneral";
            }
        }

        // Odeslání ověřovacího emailu
        User currentUser = userService.getUserByEmail(registrationDto.getEmail());
        userService.sendVerificationEmail(currentUser, getSiteURL(request));

        return "redirect:/registration?success";
    }

    private String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }

    @GetMapping("/verify")
    public String verifyUser(@Param("code") String code) {
        if (userService.verify(code)) {
            return "verify_email_success";
        } else {
            return "verify_email_fail";
        }
    }
}
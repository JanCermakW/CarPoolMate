package com.carpoolmate.carpoolmate.web;

import com.carpoolmate.carpoolmate.model.User;
import com.carpoolmate.carpoolmate.service.MailService;
import com.carpoolmate.carpoolmate.service.UserService;
import com.carpoolmate.carpoolmate.web.dto.UserRegistrationDto;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @ModelAttribute("user")
    public UserRegistrationDto userRegistrationDto() {
        return new UserRegistrationDto();
    }

    @GetMapping
    public String showRegistrationForm() {
        return "registration";
    }

    @PostMapping
    public String registerUserAccount(@ModelAttribute("user") UserRegistrationDto registrationDto, HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        if (userService.getUserByEmail(registrationDto.getEmail()) != null) {
            return "redirect:/registration?error";
        }

        if (!userService.validatePassword(registrationDto.getPassword())) {
            return "redirect:/registration?errorPasswd";
        }

        if (userService.save(registrationDto) == null) {
            return "redirect:/registration?errorGeneral";
        }

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
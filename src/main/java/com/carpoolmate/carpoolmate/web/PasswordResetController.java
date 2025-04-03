package com.carpoolmate.carpoolmate.web;

import com.carpoolmate.carpoolmate.model.User;
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

import static com.carpoolmate.carpoolmate.web.PasswordGenerator.generateRandomPassword;

@Controller
@RequestMapping("/forgotPasswd")
public class PasswordResetController {

    @Autowired
    private UserService userService;

    @ModelAttribute("user")
    public UserRegistrationDto userRegistrationDto() {
        return new UserRegistrationDto();
    }

    @GetMapping
    public String showForm() {
        return "forgot_passwd";
    }

    @PostMapping
    public String sendEmail(@ModelAttribute("user") UserRegistrationDto registrationDto, HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {

        String userEmail = registrationDto.getEmail();

        User currentUser = userService.getUserByEmail(userEmail);

        userService.sendForgotPasswd(currentUser, getSiteURL(request));

        return "redirect:/forgotPasswd?success";
    }

    private String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }

    @GetMapping("/verify")
    public String verifyUser(@Param("code") String code) throws MessagingException, UnsupportedEncodingException {
        if (userService.verifyForgotPasswd(code)) {

            User currentUser = userService.getUserByEmail(code.substring(63));

            if (currentUser != null) {
                String password = generateRandomPassword(12);
                String content = "Milý [[name]],<br>"
                        + "Zde zasíláme nové heslo, ihned po přihlášení si ho změňte!!:<br>"
                        + "<h3>[[password]]</h3>"
                        + "Děkujeme,<br>"
                        + "Realboss team.";
                String subject = "Zapomenuté heslo - Realboss";

                content = content.replace("[[name]]", currentUser.getFirstName() + " " + currentUser.getLastName());
                content = content.replace("[[password]]", password);

                userService.sendEmail(currentUser, content, subject);

                currentUser.setPassword(userService.encodePasswd(password));
                userService.updateUser(currentUser);
                return "forgot_passwd_success";
            }
            return "forgot_passwd_fail";
        } else {
            return "forgot_passwd_fail";
        }
    }
}

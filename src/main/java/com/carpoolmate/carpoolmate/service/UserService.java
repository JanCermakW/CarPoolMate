package com.carpoolmate.carpoolmate.service;

import com.carpoolmate.carpoolmate.model.Role;
import com.carpoolmate.carpoolmate.model.User;
import com.carpoolmate.carpoolmate.web.dto.UserRegistrationDto;
import jakarta.mail.MessagingException;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.List;

public interface UserService extends UserDetailsService {
    User save(UserRegistrationDto registrationDto);
    List<User> getAllUsers();

    User getUserById(Long id);
    User getUserByEmail(String email);
    User updateUser(User user);

    boolean existsById(Long id);


    User saveUserStartup(User user);

    String encodePasswd(String passwd);

    void sendVerificationEmail(User user, String siteURL) throws MessagingException, UnsupportedEncodingException;

    boolean verify(String verificationCode);

    void sendForgotPasswd(User user, String siteURL) throws MessagingException, UnsupportedEncodingException;

    boolean verifyForgotPasswd(String verificationCode);

    void sendEmail(User user, String content, String subject) throws MessagingException, UnsupportedEncodingException;
    boolean validatePassword(String password);

    User saveDriver(UserRegistrationDto registrationDto);

    List<User> getPendingDrivers();

    void approveDriver(Long driverId);

    public void deleteUserById(Long id);


}

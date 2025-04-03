package com.carpoolmate.carpoolmate.service;

import com.carpoolmate.carpoolmate.exception.UserNotFoundException;
import com.carpoolmate.carpoolmate.model.Role;
import com.carpoolmate.carpoolmate.model.User;
import com.carpoolmate.carpoolmate.repository.RoleRepository;
import com.carpoolmate.carpoolmate.repository.UserRepository;
import com.carpoolmate.carpoolmate.web.PasswordGenerator;
import com.carpoolmate.carpoolmate.web.dto.UserRegistrationDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService{

    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private JavaMailSender mailSender;

    public UserServiceImpl(UserRepository userRepository) {
        super();
        this.userRepository = userRepository;
    }

    @Override
    public User save(UserRegistrationDto registrationDto) {
        User user = new User(registrationDto.getFirstName(),
                registrationDto.getLastName(),
                registrationDto.getEmail(),
                passwordEncoder.encode(registrationDto.getPassword()),
                roleRepository.findByName(registrationDto.getRole())
        );

        String randomCode = PasswordGenerator.generateRandomPassword(64);
        user.setVerificationCode(randomCode);
        user.setEnabled(false);

        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("Špatné jméno nebo heslo!");
        }
        if (!user.isEnabled()) {
            throw new DisabledException("Účet není aktivní");
        }

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), mapRolesToAuthorities(user.getRole()));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Role role) {
        return List.of(new SimpleGrantedAuthority(role.getName().name()));
    }


    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).get();
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    @Override
    public User saveUserStartup(User user) {
        return userRepository.save(user);
    }

    @Override
    public String encodePasswd(String passwd) {
        return passwordEncoder.encode(passwd);
    }

    @Override
    public void sendEmail(User user, String content, String subject) throws MessagingException, UnsupportedEncodingException {
        String toAddress = user.getEmail();
        String fromAddress = "honzacermak74@gmail.com";
        String senderName = "Realboss.co";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        helper.setText(content, true);

        mailSender.send(message);

    }

    @Override
    public void sendVerificationEmail(User user, String siteURL) throws MessagingException, UnsupportedEncodingException {
        String content = "Milý [[name]],<br>"
                + "Kliknutím na odkaz níže ověřte svou registraci:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">OVĚŘIT</a></h3>"
                + "Děkujeme,<br>"
                + "Realboss team.";
        String subject = "Ověřte svůj email - Realboss";

        content = content.replace("[[name]]", user.getFirstName() + " " + user.getLastName());
        String verifyURL = siteURL + "/registration/verify?code=" + user.getVerificationCode();

        content = content.replace("[[URL]]", verifyURL);

        sendEmail(user, content, subject);

    }

    @Override
    public boolean verify(String verificationCode) {
        User user = userRepository.findByVerificationCode(verificationCode);

        if (user == null || user.isEnabled()) {
            return false;
        } else {
            user.setVerificationCode(null);
            user.setEnabled(true);
            userRepository.save(user);

            return true;
        }
    }

    @Override
    public void sendForgotPasswd(User user, String siteURL) throws MessagingException, UnsupportedEncodingException {
        String randomCode = PasswordGenerator.generateRandomPassword(64) + user.getEmail();
        user.setVerificationCode(randomCode);
        userRepository.save(user);

        String content = "Milý [[name]],<br>"
                + "Kliknutím na odkaz níže si necháte poslat vygenerované heslo a ihned si ho po přihlášení změňte:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">ZASLAT HESLO</a></h3>"
                + "Děkujeme,<br>"
                + "Realboss team.";
        String subject = "Zapomenuté heslo - Realboss";

        content = content.replace("[[name]]", user.getFirstName() + " " + user.getLastName());
        String verifyURL = siteURL + "/forgotPasswd/verify?code=" + user.getVerificationCode();

        content = content.replace("[[URL]]", verifyURL);

        sendEmail(user, content, subject);
    }

    @Override
    public boolean verifyForgotPasswd(String verificationCode) {
        User user = userRepository.findByVerificationCode(verificationCode);

        if (user == null) {
            return false;
        } else {
            user.setVerificationCode(null);
            userRepository.save(user);

            return true;
        }
    }

    @Override
    public boolean validatePassword(String password) {
        String regex = "^(?=.*[A-Z]).{8,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}


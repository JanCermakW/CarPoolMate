package com.carpoolmate.carpoolmate.web;

import com.carpoolmate.carpoolmate.model.ChatMessage;
import com.carpoolmate.carpoolmate.model.User;
import com.carpoolmate.carpoolmate.service.ChatOverviewService;
import com.carpoolmate.carpoolmate.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/messages")
public class ChatMenuController {
    @Autowired
    private ChatOverviewService chatOverviewService;
    @Autowired
    private UserService userService;

    @GetMapping
    public String chatMenu(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.getUserByEmail(auth.getName());
        Map<User, ChatMessage> lastMessages = chatOverviewService.getLastMessages(currentUser.getId());
        model.addAttribute("lastMessages", lastMessages);
        model.addAttribute("currentUser", currentUser);
        return "chat-menu";
    }
}

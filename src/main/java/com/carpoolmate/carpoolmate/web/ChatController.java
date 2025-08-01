package com.carpoolmate.carpoolmate.web;

import com.carpoolmate.carpoolmate.model.ChatMessage;
import com.carpoolmate.carpoolmate.model.User;
import com.carpoolmate.carpoolmate.service.ChatService;
import com.carpoolmate.carpoolmate.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/chat")
public class ChatController {
    @Autowired
    private ChatService chatService;
    @Autowired
    private UserService userService;

    @GetMapping("/{recipientId}")
    public String chatPage(@PathVariable Long recipientId, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.getUserByEmail(auth.getName());
        List<ChatMessage> messages = chatService.getConversation(currentUser.getId(), recipientId);
        model.addAttribute("messages", messages);
        model.addAttribute("recipientId", recipientId);
        return "chat";
    }

    @PostMapping("/send")
    public String sendMessage(@RequestParam Long recipientId, @RequestParam String content) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.getUserByEmail(auth.getName());
        chatService.sendMessage(currentUser.getId(), recipientId, content);
        return "redirect:/chat/" + recipientId;
    }
}

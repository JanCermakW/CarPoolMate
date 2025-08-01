package com.carpoolmate.carpoolmate.service;

import com.carpoolmate.carpoolmate.model.ChatMessage;
import com.carpoolmate.carpoolmate.model.User;
import com.carpoolmate.carpoolmate.repository.ChatMessageRepository;
import com.carpoolmate.carpoolmate.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatService {
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    @Autowired
    private UserRepository userRepository;

    public ChatMessage sendMessage(Long senderId, Long recipientId, String content) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found: " + senderId));
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new IllegalArgumentException("Recipient not found: " + recipientId));
        ChatMessage message = new ChatMessage();
        message.setSender(sender);
        message.setRecipient(recipient);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());
        message.setDelivered(true);
        message.setRead(false);
        return chatMessageRepository.save(message);
    }

    public List<ChatMessage> getConversation(Long user1Id, Long user2Id) {
        User user1 = userRepository.findById(user1Id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + user1Id));
        User user2 = userRepository.findById(user2Id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + user2Id));
        return chatMessageRepository.findConversation(user1, user2);
    }

    public void markAsRead(Long messageId) {
        ChatMessage msg = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found: " + messageId));
        msg.setRead(true);
        chatMessageRepository.save(msg);
    }

    public List<ChatMessage> getUnreadMessages(Long recipientId) {
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new IllegalArgumentException("Recipient not found: " + recipientId));
        return chatMessageRepository.findByRecipientAndReadFalse(recipient);
    }
}

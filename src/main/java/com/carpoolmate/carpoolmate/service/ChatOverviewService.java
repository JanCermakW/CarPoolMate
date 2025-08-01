package com.carpoolmate.carpoolmate.service;

import com.carpoolmate.carpoolmate.model.ChatMessage;
import com.carpoolmate.carpoolmate.model.User;
import com.carpoolmate.carpoolmate.repository.ChatMessageRepository;
import com.carpoolmate.carpoolmate.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatOverviewService {
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    @Autowired
    private UserRepository userRepository;

    /**
     * Returns a list of users with whom the current user has exchanged messages, sorted by last message timestamp desc.
     */
    public List<User> getChatPartners(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        List<ChatMessage> messages = chatMessageRepository.findAll();
        Set<User> partners = new HashSet<>();
        for (ChatMessage msg : messages) {
            if (msg.getSender().equals(user)) partners.add(msg.getRecipient());
            if (msg.getRecipient().equals(user)) partners.add(msg.getSender());
        }
        partners.remove(user);
        return partners.stream().collect(Collectors.toList());
    }

    /**
     * Returns the last message exchanged with each partner, sorted by timestamp desc.
     */
    public Map<User, ChatMessage> getLastMessages(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        List<ChatMessage> messages = chatMessageRepository.findAll();
        Map<User, ChatMessage> lastMsg = new HashMap<>();
        for (ChatMessage msg : messages) {
            User partner = null;
            if (msg.getSender().equals(user)) partner = msg.getRecipient();
            else if (msg.getRecipient().equals(user)) partner = msg.getSender();
            if (partner != null) {
                ChatMessage prev = lastMsg.get(partner);
                if (prev == null || msg.getTimestamp().isAfter(prev.getTimestamp())) {
                    lastMsg.put(partner, msg);
                }
            }
        }
        return lastMsg.entrySet().stream()
                .sorted((a, b) -> b.getValue().getTimestamp().compareTo(a.getValue().getTimestamp()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }
}

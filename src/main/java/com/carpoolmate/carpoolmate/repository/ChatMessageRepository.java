package com.carpoolmate.carpoolmate.repository;

import com.carpoolmate.carpoolmate.model.ChatMessage;
import com.carpoolmate.carpoolmate.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findBySenderAndRecipientOrderByTimestampAsc(User sender, User recipient);
    List<ChatMessage> findByRecipientAndReadFalse(User recipient);

    @Query("SELECT m FROM ChatMessage m WHERE (m.sender = :user1 AND m.recipient = :user2) OR (m.sender = :user2 AND m.recipient = :user1) ORDER BY m.timestamp ASC")
    List<ChatMessage> findConversation(User user1, User user2);
}

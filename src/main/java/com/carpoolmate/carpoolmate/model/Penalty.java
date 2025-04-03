package com.carpoolmate.carpoolmate.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "penalties")
public class Penalty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String reason;
    private LocalDateTime issuedAt;
    private Integer penaltyPoints;

    @Enumerated(EnumType.STRING)
    private PenaltyStatus status;

    public Penalty() {
        this.issuedAt = LocalDateTime.now();
        this.status = PenaltyStatus.ACTIVE; // Default status
    }

    public Long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LocalDateTime getIssuedAt() { return issuedAt; }

    public Integer getPenaltyPoints() { return penaltyPoints; }
    public void setPenaltyPoints(Integer penaltyPoints) { this.penaltyPoints = penaltyPoints; }

    public PenaltyStatus getStatus() { return status; }
    public void setStatus(PenaltyStatus status) { this.status = status; }
}


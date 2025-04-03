package com.carpoolmate.carpoolmate.model;

import jakarta.persistence.*;

import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    private String phoneNumber;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    private boolean isDriverVerified = false;  // True when an admin approves the driver
    private String idPhotoPath;  // Path to the uploaded ID card image

    @Column(name = "verification_code")
    private String verificationCode;

    private boolean enabled;

    @OneToMany(mappedBy = "reviewedUser", cascade = CascadeType.ALL)
    private List<Review> receivedReviews = new ArrayList<>();

    @Transient
    public double getAverageRating() {
        if (receivedReviews.isEmpty()) return 0.0;
        return receivedReviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
    }

    public void expirePenaltyAfterSuccess() {
        // Assuming the user has a list of penalties, find the ACTIVE penalties
        List<Penalty> activePenalties = this.penalties.stream()
                .filter(p -> p.getStatus() == PenaltyStatus.ACTIVE)
                .collect(Collectors.toList());

        // If there are at least 4 successful rides, expire the penalties
        if (activePenalties.size() >= 4) {
            // Expire the first 4 penalties
            activePenalties.subList(0, 4).forEach(p -> p.setStatus(PenaltyStatus.EXPIRED));
        }
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Penalty> penalties; // List of penalties for the user

    // Other fields, constructors, getters, and setters

    public List<Penalty> getPenalties() {
        return penalties;
    }

    public void setPenalties(List<Penalty> penalties) {
        this.penalties = penalties;
    }

    public User(Long id, String email, String password, String firstName, String lastName, String phoneNumber, Role role, boolean isDriverVerified, String idPhotoPath, String verificationCode, boolean enabled, List<Review> receivedReviews, List<Penalty> penalties) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.isDriverVerified = isDriverVerified;
        this.idPhotoPath = idPhotoPath;
        this.verificationCode = verificationCode;
        this.enabled = enabled;
        this.receivedReviews = receivedReviews;
        this.penalties = penalties;
    }

    public User(String firstName, String lastName, String email, String password, Role role) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public User() {
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isDriverVerified() {
        return isDriverVerified;
    }

    public void setDriverVerified(boolean driverVerified) {
        isDriverVerified = driverVerified;
    }

    public String getIdPhotoPath() {
        return idPhotoPath;
    }

    public void setIdPhotoPath(String idPhotoPath) {
        this.idPhotoPath = idPhotoPath;
    }

    public List<Review> getReceivedReviews() {
        return receivedReviews;
    }

    public void setReceivedReviews(List<Review> receivedReviews) {
        this.receivedReviews = receivedReviews;
    }
}


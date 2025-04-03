package com.carpoolmate.carpoolmate.model;

import jakarta.persistence.*;

import java.util.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "ride_requests")
public class RideRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ride_id", nullable = false)
    private Ride ride;

    @ManyToOne
    @JoinColumn(name = "passenger_id", nullable = false)
    private User passenger;

    @Enumerated(EnumType.STRING)
    private RequestStatus status = RequestStatus.PENDING; // Default: PENDING

    private LocalDateTime requestTime = LocalDateTime.now();

    public boolean isCancelable() {
        return this.status == RequestStatus.PENDING;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.ride.getDepartureTime().minusHours(4));
    }

    public RideRequest(Long id, Ride ride, User passenger, RequestStatus status, LocalDateTime requestTime) {
        this.id = id;
        this.ride = ride;
        this.passenger = passenger;
        this.status = status;
        this.requestTime = requestTime;
    }

    public RideRequest() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Ride getRide() {
        return ride;
    }

    public void setRide(Ride ride) {
        this.ride = ride;
    }

    public User getPassenger() {
        return passenger;
    }

    public void setPassenger(User passenger) {
        this.passenger = passenger;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public LocalDateTime getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(LocalDateTime requestTime) {
        this.requestTime = requestTime;
    }
}


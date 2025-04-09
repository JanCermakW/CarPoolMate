package com.carpoolmate.carpoolmate.service;

import com.carpoolmate.carpoolmate.model.Ride;
import com.carpoolmate.carpoolmate.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface RideService {
    public Ride createRide(Ride ride);
    public List<Ride> getAvailableRides();

    public String reserveRide(Long rideId, String username);

    List<Ride> findAll();
    List<Ride> filterRides(String startLocation, String destination, LocalDateTime from, LocalDateTime to, Double maxPrice, Integer minSeats);

    public Ride updateRide(Ride ride);

    public List<String> getLocationSuggestions(String query, String field);

    public Ride getRideById(Long id);

    public void bookRide(Long rideId);
    public List<User> getPassengersForRide(Long rideId);
    User getCurrentUser();
}

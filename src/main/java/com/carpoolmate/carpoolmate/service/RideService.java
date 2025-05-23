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

    List<Ride> getRidesByUser(User user);

    List<Ride> getRidesByDriver(User user);

    public void unreserveRide(Long rideId);
    void deleteRide(Long rideId);

    List<Ride> getPastRidesByUser(User user);

    public List<Ride> getPastDriverRidesByUser(User user);

    public List<Ride> getPastPassengerRidesByUser(User user);

    List<Ride> getPastSharedRides(User user, User user2);

    Long getRideIdByRequest(Long rideId);

    void approveRequest(Long requestId);

    void rejectRequest(Long requestId);

    List<Ride> getRidesWaitingApprove(User user);

}

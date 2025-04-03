package com.carpoolmate.carpoolmate.service;

import com.carpoolmate.carpoolmate.model.Ride;

import java.util.List;

public interface RideService {
    public Ride createRide(Ride ride);
    public List<Ride> getAvailableRides();

    public String reserveRide(Long rideId, String username);
}

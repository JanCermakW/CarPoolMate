package com.carpoolmate.carpoolmate.service;

import com.carpoolmate.carpoolmate.model.Ride;
import com.carpoolmate.carpoolmate.repository.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Service
public class RideServiceImpl implements RideService{

    @Autowired
    private RideRepository rideRepository;

    @Override
    public Ride createRide(Ride ride) {
        return rideRepository.save(ride);
    }

    @Override
    public List<Ride> getAvailableRides() {
        return rideRepository.findByAvailable(true);
    }

    @Override
    public String reserveRide(Long rideId, String username) {
        return null;
    }

    @Override
    public List<Ride> findAll() {
        return rideRepository.findAll();
    }

    @Override
    public List<Ride> filterRides(String startLocation, String destination, LocalDateTime from, LocalDateTime to, Double maxPrice, Integer minSeats) {
        return rideRepository.filterRides(startLocation, destination, from, to, maxPrice, minSeats);
    }

    @Override
    public Ride updateRide(Ride ride) {
        return rideRepository.save(ride);
    }

    public List<String> getLocationSuggestions(String query, String field) {
        if (field.equals("startLocation")) {
            // Here you can retrieve locations from the database that match the query
            return rideRepository.findDistinctLocations(query);
        } else if (field.equals("destination")) {
            return rideRepository.findDistinctDestinations(query);
        }
        return rideRepository.findDistinctLocations(query);
    }
}

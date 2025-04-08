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
    public List<Ride> filterRides(String startLocation, String destination, LocalDate departureDate, Double maxPrice, Integer minSeats) {
        LocalDateTime startOfDay = null;
        if (departureDate != null) {
            startOfDay = departureDate.atStartOfDay();
        }
        return rideRepository.filterRides(startLocation, destination, startOfDay, maxPrice, minSeats);
    }
}

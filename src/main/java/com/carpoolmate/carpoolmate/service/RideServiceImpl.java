package com.carpoolmate.carpoolmate.service;

import com.carpoolmate.carpoolmate.model.Ride;
import com.carpoolmate.carpoolmate.model.User;
import com.carpoolmate.carpoolmate.repository.RideRepository;
import com.carpoolmate.carpoolmate.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
public class RideServiceImpl implements RideService{

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private UserRepository userRepository;

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

    @Override
    public Ride getRideById(Long id) {
        return rideRepository.getRideById(id);
    }

    @Override
    public void bookRide(Long rideId) {
        // Načtěte jízdu podle ID
        Ride ride = rideRepository.findById(rideId).orElseThrow(() -> new IllegalArgumentException("Jízda nenalezena"));

        if (ride.getAvailableSeats() <= 0) {
            throw new IllegalStateException("Žádné volné místo k dispozici");
        }

        // Získejte aktuálního uživatele z kontextu zabezpečení
        User currentUser = getCurrentUser();

        // Zkontrolujte, zda uživatel není již pasažérem této jízdy
        if (ride.getPassengers().contains(currentUser)) {
            throw new IllegalStateException("Jste již pasažérem této jízdy");
        }

        if (ride.getDriver() == currentUser) {
            throw new IllegalStateException("Nemůžete se zaregistrovat na svoji jízdu");
        }

        // Přidání pasažéra do seznamu pasažérů
        ride.getPassengers().add(currentUser);

        // Snížení počtu volných míst
        ride.setAvailableSeats(ride.getAvailableSeats() - 1);

        // Uložení jízdy
        rideRepository.save(ride);
    }

    @Override
    public List<User> getPassengersForRide(Long rideId) {
        Ride ride = rideRepository.findById(rideId).orElseThrow(() -> new IllegalArgumentException("Jízda nenalezena"));

        // Vrátit seznam pasažérů
        return ride.getPassengers();
    }

    @Override
    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            return userRepository.findByEmail(username); // Zde e-mail slouží jako uživatelské jméno
        } else {
            throw new IllegalStateException("Nepřihlášený uživatel");
        }
    }


}

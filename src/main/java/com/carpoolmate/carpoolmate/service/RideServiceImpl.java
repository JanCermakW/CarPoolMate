package com.carpoolmate.carpoolmate.service;

import com.carpoolmate.carpoolmate.model.RequestStatus;
import com.carpoolmate.carpoolmate.model.Ride;
import com.carpoolmate.carpoolmate.model.RideRequest;
import com.carpoolmate.carpoolmate.model.User;
import com.carpoolmate.carpoolmate.repository.RideRepository;
import com.carpoolmate.carpoolmate.repository.UserRepository;
import com.carpoolmate.carpoolmate.repository.RideRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.time.LocalTime.now;

@Service
public class RideServiceImpl implements RideService{

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RideRequestRepository rideRequestRepository;

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

        if (ride.isRequiresApproval()) {
            boolean alreadyRequested = ride.getRideRequests().stream()
                    .anyMatch(req -> req.getPassenger().equals(currentUser));
            if (alreadyRequested) {
                throw new RuntimeException("O tuto jízdu jste již zažádali.");
            }

            RideRequest request = new RideRequest();
            request.setPassenger(currentUser);
            request.setRide(ride);
            request.setStatus(RequestStatus.PENDING);
            rideRequestRepository.save(request);
        } else {
            ride.getPassengers().add(currentUser);
            ride.setAvailableSeats(ride.getAvailableSeats() - 1);
            rideRepository.save(ride);
        }
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

    @Override
    public List<Ride> getRidesByUser(User user) {
        List<Ride> all = rideRepository.findByPassengersContaining(user);
        return all.stream()
                .filter(ride -> ride.getDepartureTime().isAfter(LocalDateTime.now()))
                .toList();
    }

    @Override
    public List<Ride> getRidesByDriver(User user) {

        List<Ride> all = rideRepository.findByDriverId(user.getId());
        return all.stream()
                .filter(ride -> ride.getDepartureTime().isAfter(LocalDateTime.now()))
                .toList();
    }

    @Override
    public void unreserveRide(Long rideId) {
        // Načtěte jízdu podle ID
        Ride ride = rideRepository.findById(rideId).orElseThrow(() -> new IllegalArgumentException("Jízda nenalezena"));

        User currentUser = getCurrentUser();

        if (ride.getDepartureTime().isBefore(LocalDateTime.now().plusHours(24))) {
            throw new IllegalStateException("Nelze provést změnu méně než 24 hodin před odjezdem.");
        }

        // Přidání pasažéra do seznamu pasažérů
        ride.getPassengers().remove(currentUser);

        // Snížení počtu volných míst
        ride.setAvailableSeats(ride.getAvailableSeats() + 1);

        // Uložení jízdy
        rideRepository.save(ride);
    }

    @Override
    public void deleteRide(Long rideId) {
        Ride ride = rideRepository.findById(rideId).orElseThrow(() -> new IllegalArgumentException("Jízda nenalezena"));

        rideRepository.delete(ride);
    }

    @Override
    public List<Ride> getPastDriverRidesByUser(User user) {
        List<Ride> asDriver = rideRepository.findByDriverId(user.getId());

        LocalDateTime now = LocalDateTime.now();

        return asDriver.stream()
                .filter(ride -> ride.getDepartureTime().isBefore(now))
                .distinct()
                .toList();
    }

    @Override
    public List<Ride> getPastPassengerRidesByUser(User user) {
        List<Ride> asPassenger = rideRepository.findByPassengersContaining(user);

        LocalDateTime now = LocalDateTime.now();

        return asPassenger.stream()
                .filter(ride -> ride.getDepartureTime().isBefore(now))
                .distinct()
                .toList();
    }

    @Override
    public List<Ride> getPastRidesByUser(User user) {
        List<Ride> asPassenger = rideRepository.findByPassengersContaining(user);
        List<Ride> asDriver = rideRepository.findByDriverId(user.getId());

        LocalDateTime now = LocalDateTime.now();

        return Stream.concat(asPassenger.stream(), asDriver.stream())
                .filter(ride -> ride.getDepartureTime().isBefore(now))
                .distinct() // avoids duplicates in case user is both driver & passenger (unlikely but safe)
                .toList();
    }



    @Override
    public List<Ride> getPastSharedRides(User user, User user2) {
        return rideRepository.findPastSharedRides(user, user2);
    }

    @Override
    public Long getRideIdByRequest(Long requestId) {
        RideRequest request = rideRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Žádost nenalezena"));

        return request.getRide().getId();
    }

    @Override
    public void approveRequest(Long requestId) {
        RideRequest request = rideRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Žádost nenalezena"));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Žádost není ve stavu čekání na vyřízení");
        }

        Ride ride = request.getRide();
        User user = request.getPassenger();

        // Approve the request
        request.setStatus(RequestStatus.ACCEPTED);

        // Add user to the ride's passengers list
        ride.getPassengers().add(user);

        ride.setAvailableSeats(ride.getAvailableSeats() - 1);

        // Save changes
        rideRequestRepository.delete(request);
        rideRepository.save(ride);
    }

    @Override
    public void rejectRequest(Long requestId) {
        RideRequest request = rideRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Žádost nenalezena"));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Pouze čekající mohou být odmítnuty");
        }

        request.setStatus(RequestStatus.REJECTED);
        rideRequestRepository.save(request);
    }

    @Override
    public List<Ride> getRidesWaitingApprove(User user) {
        List<RideRequest> requests = rideRequestRepository.findByPassengerId(user.getId());
        List<Ride> rides = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (RideRequest request : requests) {
            Ride ride = request.getRide();
            if (request.getStatus() == RequestStatus.PENDING && ride.getDepartureTime().isAfter(now)) {
                rides.add(ride);
            }
        }

        return rides;
    }


}

package com.carpoolmate.carpoolmate.repository;

import com.carpoolmate.carpoolmate.model.Ride;
import com.carpoolmate.carpoolmate.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface RideRepository extends JpaRepository<Ride, Long> {
    List<Ride> findByDriverId(Long driverId);
    List<Ride> findByPassengers(List<User> passengers);
    Optional<Ride> findByIdAndDriverId(Long rideId, Long driverId);
    List<Ride> findByStartLocationAndDestination(String startLocation, String destination);

    List<Ride> findByAvailable(boolean available);
}

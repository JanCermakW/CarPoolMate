package com.carpoolmate.carpoolmate.repository;

import com.carpoolmate.carpoolmate.model.Ride;
import com.carpoolmate.carpoolmate.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RideRepository extends JpaRepository<Ride, Long> {
    List<Ride> findByDriverId(Long driverId);
    List<Ride> findByPassengers(List<User> passengers);
    Optional<Ride> findByIdAndDriverId(Long rideId, Long driverId);
    List<Ride> findByStartLocationAndDestination(String startLocation, String destination);

    List<Ride> findByAvailable(boolean available);


    @Query("SELECT r FROM Ride r WHERE " +
            "(:startLocation IS NULL OR LOWER(r.startLocation) LIKE LOWER(CONCAT('%', :startLocation, '%'))) AND " +
            "(:destination IS NULL OR LOWER(r.destination) LIKE LOWER(CONCAT('%', :destination, '%'))) AND " +
            "(:departureDate IS NULL OR r.departureTime >= :departureDate) AND " +
            "(:maxPrice IS NULL OR r.pricePerSeat <= :maxPrice) AND " +
            "(:minSeats IS NULL OR r.availableSeats >= :minSeats)")
    List<Ride> filterRides(
            String startLocation,
            String destination,
            LocalDateTime departureDate,
            Double maxPrice,
            Integer minSeats
    );

}

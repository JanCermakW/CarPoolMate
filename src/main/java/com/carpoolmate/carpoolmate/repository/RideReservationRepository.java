package com.carpoolmate.carpoolmate.repository;

import com.carpoolmate.carpoolmate.model.RideRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RideReservationRepository extends JpaRepository<RideRequest, Long> {
    List<RideRequest> findByRideId(Long rideId);
    List<RideRequest> findByPassengerId(Long passengerId);
}

package com.carpoolmate.carpoolmate.repository;

import com.carpoolmate.carpoolmate.model.RideRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RideRequestRepository extends JpaRepository<RideRequest, Long> {
    Optional<RideRequest> findById(Long rideRequestId);

    List<RideRequest> findByPassengerId(Long passengerId);

    void deleteByPassengerId(Long passengerId);
}

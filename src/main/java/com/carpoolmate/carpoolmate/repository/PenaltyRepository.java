package com.carpoolmate.carpoolmate.repository;

import com.carpoolmate.carpoolmate.model.Penalty;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PenaltyRepository extends JpaRepository<Penalty, Long> {
    List<Penalty> findByUserId(Long userId);
}
package com.carpoolmate.carpoolmate.repository;

import com.carpoolmate.carpoolmate.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}

package com.carpoolmate.carpoolmate.repository;

import com.carpoolmate.carpoolmate.model.Review;
import com.carpoolmate.carpoolmate.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByReviewedUser(User user);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.reviewedUser = :user")
    Double findAverageRatingByReviewedUser(@Param("user") User user);

    List<Review> findByReviewerAndReviewedUser(User reviewer, User reviewedUser);
}

package com.carpoolmate.carpoolmate.service;

import com.carpoolmate.carpoolmate.model.Review;
import com.carpoolmate.carpoolmate.model.User;

import java.util.List;

public interface ReviewService {
    void saveReview(Review review);

    List<Review> getReviewsByUser(User user);

    Double getRatingsAvgByUser(User user);

    List<Review> getReviewsByReviewerAndReviewed(User reviewer, User reviewed);
}

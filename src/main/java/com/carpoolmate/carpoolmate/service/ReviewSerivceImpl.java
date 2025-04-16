package com.carpoolmate.carpoolmate.service;

import com.carpoolmate.carpoolmate.model.Review;
import com.carpoolmate.carpoolmate.model.User;
import com.carpoolmate.carpoolmate.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewSerivceImpl implements ReviewService {


    @Autowired
    public ReviewRepository reviewRepository;

    @Override
    public void saveReview(Review review) {
        reviewRepository.save(review);
    }

    @Override
    public List<Review> getReviewsByUser(User user) {
        return reviewRepository.findByReviewedUser(user);
    }

    @Override
    public Double getRatingsAvgByUser(User user) {
        Double avg = reviewRepository.findAverageRatingByReviewedUser(user);
        return avg != null ? avg : 0.0;
    }

    @Override
    public List<Review> getReviewsByReviewerAndReviewed(User reviewer, User reviewed) {
        return reviewRepository.findByReviewerAndReviewedUser(reviewer, reviewed);
    }
}

package com.carpoolmate.carpoolmate.web;

import com.carpoolmate.carpoolmate.model.Review;
import com.carpoolmate.carpoolmate.model.User;
import com.carpoolmate.carpoolmate.repository.ReviewRepository;
import com.carpoolmate.carpoolmate.service.ReviewService;
import com.carpoolmate.carpoolmate.service.RideService;
import com.carpoolmate.carpoolmate.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/reviews")
public class ReviewsController {

    private final UserService userService;
    private final RideService rideService;
    private final ReviewService reviewService;

    @Autowired
    public ReviewsController(UserService userService, RideService rideService, ReviewService reviewService) {
        this.userService = userService;
        this.rideService = rideService;
        this.reviewService = reviewService;
    }

    @Operation(summary = "Submit a review", description = "Saves a review for a specific user, ensuring no duplicates within 2 months.")
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "Redirected after successful submission or validation failure"),
            @ApiResponse(responseCode = "400", description = "Invalid review data or duplicate within restricted timeframe")
    })
    @PostMapping
    public String saveReview(@ModelAttribute Review review,
                             @RequestParam("reviewedUserId") Long reviewedUserId,
                             Principal principal,
                             RedirectAttributes redirectAttributes) {

        User reviewer = userService.getUserByEmail(principal.getName());
        User reviewed = userService.getUserById(reviewedUserId);

        // Check for existing review within the last 2 months
        List<Review> existingReviews = reviewService.getReviewsByReviewerAndReviewed(reviewer, reviewed);
        boolean reviewedRecently = existingReviews.stream()
                .anyMatch(r -> r.getCreatedAt().isAfter(LocalDateTime.now().minusMonths(2)));

        if (reviewedRecently) {
            redirectAttributes.addFlashAttribute("errorMessage", "Uživatel byl již recenzován v posledních 2 měsících.");
            return "redirect:/profiles/" + reviewedUserId;
        }

        // Proceed with saving review
        review.setReviewer(reviewer);
        review.setReviewedUser(reviewed);
        review.setCreatedAt(LocalDateTime.now());

        reviewService.saveReview(review);

        redirectAttributes.addFlashAttribute("successMessage", "Recenze byla úspěšně odeslána.");
        return "redirect:/profiles/" + reviewedUserId;
    }
}

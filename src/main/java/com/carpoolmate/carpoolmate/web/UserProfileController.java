package com.carpoolmate.carpoolmate.web;

import com.carpoolmate.carpoolmate.model.Review;
import com.carpoolmate.carpoolmate.model.Ride;
import com.carpoolmate.carpoolmate.model.User;
import com.carpoolmate.carpoolmate.service.ReviewService;
import com.carpoolmate.carpoolmate.service.RideService;
import com.carpoolmate.carpoolmate.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/profiles")
public class UserProfileController {

    private final UserService userService;

    private final ReviewService reviewService;

    public UserProfileController(UserService userService, ReviewService reviewService, RideService rideService) {
        this.userService = userService;
        this.reviewService = reviewService;
        this.rideService = rideService;
    }

    private final RideService rideService;

    @Operation(summary = "View public user profile", description = "Displays the public profile of a specific user, including rides and reviews.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile data displayed successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public String getUserProfile(@PathVariable Long id, Model model, Principal principal) {
        User profileUser = userService.getUserById(id);
        User currentUser = userService.getUserByEmail(principal.getName());

        boolean canWriteReview = !rideService.getPastSharedRides(profileUser, currentUser).isEmpty();

        int ridesAsDriver = rideService.getPastDriverRidesByUser(profileUser).size();

        int ridesAsPassenger = rideService.getPastPassengerRidesByUser(profileUser).size();

        int reviewsCount = reviewService.getReviewsByUser(profileUser).size();

        double ratingsAvg = reviewService.getRatingsAvgByUser(profileUser);

        List<Ride> pastSharedRides = rideService.getPastSharedRides(profileUser, currentUser).stream()
                .sorted(Comparator.comparing(Ride::getDepartureTime).reversed())
                .limit(5)
                .collect(Collectors.toList());

        List<Review> receivedReviews = reviewService.getReviewsByUser(profileUser);

        model.addAttribute("canWriteReview", canWriteReview);
        model.addAttribute("profileUser", profileUser);
        model.addAttribute("pastRides", pastSharedRides);
        model.addAttribute("ridesAsDriver", ridesAsDriver);
        model.addAttribute("ridesAsPassenger", ridesAsPassenger);
        model.addAttribute("reviewsCount", reviewsCount);
        model.addAttribute("ratingsAvg", ratingsAvg);
        model.addAttribute("receivedReviews", receivedReviews);
        model.addAttribute("currentUser", currentUser);

        return "publicProfile"; // your Thymeleaf template name
    }
}

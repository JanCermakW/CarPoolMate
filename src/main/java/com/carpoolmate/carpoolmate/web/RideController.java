package com.carpoolmate.carpoolmate.web;

import com.carpoolmate.carpoolmate.model.Ride;
import com.carpoolmate.carpoolmate.model.RideRequest;
import com.carpoolmate.carpoolmate.model.RequestStatus;
import com.carpoolmate.carpoolmate.model.User;
import com.carpoolmate.carpoolmate.service.RideService;
import com.carpoolmate.carpoolmate.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * RideController is responsible for handling HTTP requests related to ride management.
 * It covers functionality such as:
 * - Viewing all rides
 * - Filtering rides based on user criteria
 * - Creating, booking, unbooking and deleting rides
 * - Viewing ride details, passengers, and ride requests
 * - Approving or rejecting ride requests
 *
 * Services used:
 * - RideService for business logic related to rides
 * - UserService for fetching user-related data
 *
 * Authenticated user is retrieved using SecurityContextHolder.
 */
@Controller
public class RideController {

    private static final Logger logger = LoggerFactory.getLogger(RideController.class);

    @Autowired
    private RideService rideService;
    @Autowired
    private UserService userService;

    /**
     * Displays a list of all available rides on the homepage.
     *
     * @param model the model to populate with ride data
     * @return view name for the homepage
     */
    @Operation(summary = "Get all rides", description = "Returns a list of all available rides.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List returned successfully")
    })
    @GetMapping("/")
    public String showAllRides(Model model) {
        List<Ride> rides = rideService.findAll();
        model.addAttribute("rides", rides);
        return "index";
    }

    /**
     * Filters rides based on various parameters like location, time, price, etc.
     * Also handles sorting and pagination of results.
     *
     * @param startLocation  optional start location
     * @param destination    optional destination
     * @param departureDate  optional departure date
     * @param departureTime  optional departure time
     * @param maxPrice       optional max price per seat
     * @param minSeats       optional minimum number of seats
     * @param sortBy         sorting criteria (e.g., priceAsc, dateDesc)
     * @param page           pagination - page number
     * @param size           pagination - page size
     * @param model          model to pass data to view
     * @return view name for filtered ride list
     */
    @Operation(summary = "Filter rides", description = "Filters available rides based on user input.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Filtered list returned"),
            @ApiResponse(responseCode = "400", description = "Invalid filter parameters")
    })
    @GetMapping("/rides/filter")
    public String filterRides(
            @RequestParam(required = false) String startLocation,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate departureDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "HH:mm") LocalTime departureTime,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Integer minSeats,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "6") int size,
            Model model) {

        logger.info("GET /rides/filter called");

        LocalDateTime from = null;
        LocalDateTime to = null;

        if (departureDate != null) {
            from = departureDate.atStartOfDay();
            if (departureTime != null) {
                from = LocalDateTime.of(departureDate, departureTime);
            }
            to = departureDate.plusDays(1).atStartOfDay();
        }

        List<Ride> allRides = rideService.filterRides(startLocation, destination, from, to, maxPrice, minSeats);

        // Sort results
        if (sortBy != null) {
            switch (sortBy) {
                case "priceAsc" -> allRides.sort(Comparator.comparing(Ride::getPricePerSeat));
                case "priceDesc" -> allRides.sort(Comparator.comparing(Ride::getPricePerSeat).reversed());
                case "nameAsc" -> allRides.sort(Comparator.comparing(r -> r.getDriver().getFirstName() + r.getDriver().getLastName()));
                case "nameDesc" -> allRides.sort(Comparator.comparing((Ride r) -> r.getDriver().getFirstName() + r.getDriver().getLastName()).reversed());
                case "dateAsc" -> allRides.sort(Comparator.comparing(Ride::getDepartureTime));
                case "dateDesc" -> allRides.sort(Comparator.comparing(Ride::getDepartureTime).reversed());
            }
        } else {
            Collections.shuffle(allRides); // Default random
        }

        // Pagination
        int start = (page - 1) * size;
        int end = Math.min(start + size, allRides.size());
        List<Ride> pagedRides = allRides.subList(Math.min(start, allRides.size()), end);
        int totalPages = (int) Math.ceil((double) allRides.size() / size);

        // Pass attributes to the model
        model.addAttribute("rides", pagedRides);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("size", size);
        model.addAttribute("startLocation", startLocation);
        model.addAttribute("destination", destination);
        model.addAttribute("departureDate", departureDate);
        model.addAttribute("departureTime", departureTime);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("minSeats", minSeats);

        return "rides";
    }

    /**
     * Displays the form to create a new ride.
     *
     * @param model model to hold the ride form object
     * @return view name for the create ride form
     */
    @Operation(summary = "Show ride creation form", description = "Displays the form to create a new ride.")
    @ApiResponse(responseCode = "200", description = "Form displayed")
    @GetMapping("/rides/create")
    public String showCreateRideForm(Model model) {
        model.addAttribute("ride", new Ride());
        return "create_ride";
    }

    /**
     * Handles the submission of the ride creation form.
     * Saves a new ride to the database.
     *
     * @param ride ride object populated from the form
     * @return redirect to the filtered rides view with a success message
     */
    @Operation(summary = "Create new ride", description = "Processes the creation form and stores a new ride.")
    @ApiResponse(responseCode = "302", description = "Ride created and user redirected")
    @PostMapping("/rides/create")
    public String processCreateRideForm(@ModelAttribute Ride ride) {
        ride.setAvailable(true);

        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        User driver = userService.getUserByEmail(currentUser);
        ride.setDriver(driver);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        ride.setFormattedDepartureTime(ride.getDepartureTime().format(formatter));

        rideService.createRide(ride);
        return "redirect:/rides/filter?successRideCreate";
    }

    /**
     * Shows details of a specific ride including driver and time.
     *
     * @param id    ID of the ride
     * @param model model to hold ride detail attributes
     * @return view name for ride details
     */
    @Operation(summary = "View ride details", description = "Displays the detail of a specific ride.")
    @ApiResponse(responseCode = "200", description = "Ride details displayed")
    @GetMapping("/rides/{id}")
    public String getRideDetails(@PathVariable Long id, Model model) {
        Ride ride = rideService.getRideById(id);
        if (ride == null) {
            return "redirect:/rides?error=notfound";
        }

        User currentUser = rideService.getCurrentUser();
        boolean isDriver = ride.getDriver().getEmail().equals(currentUser.getEmail());

        model.addAttribute("ride", ride);
        model.addAttribute("isDriver", isDriver);
        return "ride-details";
    }

    /**
     * Books a ride for the currently logged-in user.
     *
     * @param id                 ID of the ride
     * @param model              model to show error if needed
     * @param redirectAttributes used for passing success/error messages
     * @return redirect to ride filter page
     */
    @Operation(summary = "Book a ride", description = "Reserves a ride for the logged-in user.")
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "Redirected after booking"),
            @ApiResponse(responseCode = "400", description = "Booking error occurred")
    })
    @PostMapping("/rides/book/{id}")
    public String bookRide(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            rideService.bookRide(id);
            return "redirect:/rides/filter?success=reserved";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            logger.error("Error při rezervaci jízdy", e);
            return "redirect:/rides/filter"; // Chyba při rezervaci
        }
    }

    /**
     * Shows a list of passengers for a specific ride (visible to the driver).
     *
     * @param id    ID of the ride
     * @param model model to hold passenger data
     * @return view with list of passengers or redirect with error
     */
    @Operation(summary = "View passengers", description = "Displays a list of passengers for a specific ride.")
    @ApiResponse(responseCode = "200", description = "Passenger list displayed")
    @GetMapping("/rides/{id}/passengers")
    public String viewPassengers(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("passengers", rideService.getPassengersForRide(id));
            return "ride-passengers";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            logger.error("Error při získávání pasažérů", e);
            return "redirect:/rides/{id}/passengers?error=passengersNotFound";
        }
    }

    /**
     * Cancels a ride reservation for the current user.
     *
     * @param id                 ride ID
     * @param model              model to show error if needed
     * @param redirectAttributes used for passing success/error messages
     * @return redirect to user dashboard
     */
    @Operation(summary = "Unbook a ride", description = "Cancels the current user's ride reservation.")
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "Reservation canceled and redirected"),
            @ApiResponse(responseCode = "400", description = "Error canceling reservation")
    })
    @PostMapping("/rides/unreserve/{id}")
    public String unreserveRide(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            rideService.unreserveRide(id); // Logika oddělání rezervace v RideService
            redirectAttributes.addFlashAttribute("successMessage", "Rezervace byla úspěšně zrušena.");
            return "redirect:/user?successUnreserved";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            logger.error("Error při odrezervování", e);
            return "redirect:/user?errorUnreserved"; // Chyba při rezervaci
        }
    }

    /**
     * Deletes a ride (only accessible to the driver).
     *
     * @param id                 ID of the ride
     * @param model              model for error display
     * @param redirectAttributes success or error message passing
     * @return redirect to user dashboard
     */
    @Operation(summary = "Delete ride", description = "Deletes a specific ride.")
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "Ride deleted and redirected"),
            @ApiResponse(responseCode = "400", description = "Error deleting ride")
    })
    @PostMapping("/rides/delete/{id}")
    public String deleteRide(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            rideService.deleteRide(id); // Logika oddělání rezervace v RideService
            redirectAttributes.addFlashAttribute("successMessage", "Jízda úspěšně odstraněna.");
            return "redirect:/user";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            logger.error("Error při mazání jízdy", e);
            return "redirect:/user";
        }
    }

    /**
     * Displays ride requests for a specific ride (for the driver).
     *
     * @param id    ride ID
     * @param model model to hold request data
     * @return view for ride requests
     */
    @Operation(summary = "View ride requests", description = "Shows ride requests awaiting approval.")
    @ApiResponse(responseCode = "200", description = "Ride requests displayed")
    @GetMapping("/rides/{id}/requests")
    public String viewRequests(@PathVariable Long id, Model model) {
        Ride ride = rideService.getRideById(id);
        List<RideRequest> filteredRequests = ride.getRideRequests()
                .stream()
                .filter(req -> req.getStatus() != RequestStatus.REJECTED)
                .collect(Collectors.toList());

        model.addAttribute("requests", filteredRequests);
        model.addAttribute("ride", ride);
        return "ride-requests";
    }

    /**
     * Approves a ride request (driver action).
     *
     * @param id                 request ID
     * @param redirectAttributes for success message
     * @return redirect to ride requests view
     */
    @Operation(summary = "Approve request", description = "Approves a ride request.")
    @ApiResponse(responseCode = "302", description = "Request approved and redirected")
    @PostMapping("/rides/requests/{id}/approve")
    public String approveRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Long rideId = rideService.getRideIdByRequest(id);
        rideService.approveRequest(id);
        redirectAttributes.addFlashAttribute("successMessage", "Žádost byla schválena.");
        return "redirect:/rides/" + rideId + "/requests";
    }

    /**
     * Rejects a ride request (driver action).
     *
     * @param id                 request ID
     * @param redirectAttributes for success message
     * @return redirect to ride requests view
     */
    @Operation(summary = "Reject request", description = "Rejects a ride request.")
    @ApiResponse(responseCode = "302", description = "Request rejected and redirected")
    @PostMapping("/rides/requests/{id}/reject")
    public String rejectRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        rideService.rejectRequest(id);
        redirectAttributes.addFlashAttribute("successMessage", "Žádost byla zamítnuta.");
        return "redirect:/rides/" + rideService.getRideIdByRequest(id) + "/requests";
    }

}



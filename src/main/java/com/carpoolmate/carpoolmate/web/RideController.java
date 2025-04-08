package com.carpoolmate.carpoolmate.web;

import com.carpoolmate.carpoolmate.model.Ride;
import com.carpoolmate.carpoolmate.model.User;
import com.carpoolmate.carpoolmate.service.RideService;
import com.carpoolmate.carpoolmate.service.UserService;
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
import java.util.List;

@Controller
public class RideController {

    @Autowired
    private RideService rideService;
    @Autowired
    private UserService userService;

    // Homepage – list all rides
    @GetMapping("/")
    public String showAllRides(Model model) {
        List<Ride> rides = rideService.findAll();
        rides.forEach(ride -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            ride.setFormattedDepartureTime(ride.getDepartureTime().format(formatter));
            rideService.updateRide(ride);
        });
        model.addAttribute("rides", rides);

        return "index";
    }

    @GetMapping("/rides/filter")
    public String filterRides(
            @RequestParam(required = false) String startLocation,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate departureDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "HH:mm") LocalTime departureTime,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Integer minSeats,
            Model model) {

        LocalDateTime from = null;
        LocalDateTime to = null;

        // Handling the departure date and time
        if (departureDate != null) {
            from = departureDate.atStartOfDay();  // Start of the selected date
            if (departureTime != null) {
                from = LocalDateTime.of(departureDate, departureTime);  // Set custom time for the start
            }
            to = departureDate.plusDays(1).atStartOfDay();  // Upper bound (exclusive), to the start of the next day
        }

        List<Ride> rides = rideService.filterRides(startLocation, destination, from, to, maxPrice, minSeats);
        model.addAttribute("rides", rides);
        return "index";
    }

    @GetMapping("/rides/create")
    public String showCreateRideForm(Model model) {
        model.addAttribute("ride", new Ride());
        return "create_ride";
    }

    @PostMapping("/rides/create")
    public String processCreateRideForm(@ModelAttribute Ride ride) {
        // Nastavíme výchozí hodnoty
        ride.setAvailable(true);

        // Získáme aktuálního uživatele
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

        User driver = new User();
        driver = userService.getUserByEmail(currentUser);

        // Nastavíme uživatele jako řidiče
        ride.setDriver(driver);

        // Uložíme jízdu
        rideService.createRide(ride);
        return "redirect:/";
    }


}


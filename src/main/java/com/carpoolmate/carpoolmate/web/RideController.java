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

        // Přidáváme hodnoty filtrů do modelu
        model.addAttribute("startLocation", startLocation);
        model.addAttribute("destination", destination);
        model.addAttribute("departureDate", departureDate);
        model.addAttribute("departureTime", departureTime);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("minSeats", minSeats);


        return "rides";
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
        return "redirect:/?successRideCreate";
    }

    @GetMapping("/rides/{id}")
    public String getRideDetails(@PathVariable Long id, Model model) {
        Ride ride = rideService.getRideById(id);

        if (ride == null) {
            return "redirect:/rides?error=notfound"; // Jízda nenalezena
        }

        // Načtení aktuálního uživatele
        User currentUser = rideService.getCurrentUser();

        // Zjistíme, jestli je aktuálně přihlášený uživatel řidičem jízdy
        boolean isDriver = ride.getDriver().getEmail().equals(currentUser.getEmail());

        model.addAttribute("ride", ride); // Ride objekt
        model.addAttribute("isDriver", isDriver); // Pokud je uživatel řidičem
        return "ride-details";

    }

    @PostMapping("/rides/book/{id}")
    public String bookRide(@PathVariable Long id, Model model
    ) {
        try {
            rideService.bookRide(id); // Logika rezervace v RideService
            return "redirect:/rides/{id}?success=reserved";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/rides/{id}?error=bookingFailed"; // Chyba při rezervaci
        }
    }

    // Zobrazení seznamu pasažérů pro řidiče
    @GetMapping("/rides/{id}/passengers")
    public String viewPassengers(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("passengers", rideService.getPassengersForRide(id));
            return "ride-passengers";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/rides/{id}/passengers?error=passengersNotFound";
        }
    }



}


package com.carpoolmate.carpoolmate.web;

import com.carpoolmate.carpoolmate.model.Ride;
import com.carpoolmate.carpoolmate.model.RideRequest;
import com.carpoolmate.carpoolmate.model.RequestStatus;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "6") int size,
            Model model) {

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

        // Sort
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

        // Model attributes
        model.addAttribute("rides", pagedRides);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("size", size);

        // Keep filter fields filled in
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

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        ride.setFormattedDepartureTime(ride.getDepartureTime().format(formatter));

        // Uložíme jízdu
        rideService.createRide(ride);
        return "redirect:/rides/filter?successRideCreate";
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
    public String bookRide(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes
    ) {
        try {
            rideService.bookRide(id); // Logika rezervace v RideService
            return "redirect:/rides/filter?success=reserved";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/rides/filter"; // Chyba při rezervaci
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

    @PostMapping("/rides/unreserve/{id}")
    public String unreserveRide(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes
    ) {
        try {
            rideService.unreserveRide(id); // Logika oddělání rezervace v RideService
            redirectAttributes.addFlashAttribute("successMessage", "Rezervace byla úspěšně zrušena.");
            return "redirect:/user?successUnreserved";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/user?errorUnreserved"; // Chyba při rezervaci
        }
    }


    @PostMapping("/rides/delete/{id}")
    public String deleteRide(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes
    ) {
        try {
            rideService.deleteRide(id); // Logika oddělání rezervace v RideService
            redirectAttributes.addFlashAttribute("successMessage", "Jízda úspěšně odstraněna.");
            return "redirect:/user";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/user";
        }
    }


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

    @PostMapping("/rides/requests/{id}/approve")
    public String approveRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Long rideId = rideService.getRideIdByRequest(id);
        rideService.approveRequest(id);
        redirectAttributes.addFlashAttribute("successMessage", "Žádost byla schválena.");
        return "redirect:/rides/" + rideId + "/requests";
    }

    @PostMapping("/rides/requests/{id}/reject")
    public String rejectRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        rideService.rejectRequest(id);
        redirectAttributes.addFlashAttribute("successMessage", "Žádost byla zamítnuta.");
        return "redirect:/rides/" + rideService.getRideIdByRequest(id) + "/requests";
    }


}


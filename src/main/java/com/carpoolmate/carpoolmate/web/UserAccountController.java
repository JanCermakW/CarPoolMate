package com.carpoolmate.carpoolmate.web;

import com.carpoolmate.carpoolmate.model.Ride;
import com.carpoolmate.carpoolmate.model.Role;
import com.carpoolmate.carpoolmate.model.User;
import com.carpoolmate.carpoolmate.service.FileStorageService;
import com.carpoolmate.carpoolmate.service.RoleService;
import com.carpoolmate.carpoolmate.service.UserService;
import com.carpoolmate.carpoolmate.service.RideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;

@Controller
public class UserAccountController {

    @Autowired
    private RoleService roleService;
    @Autowired
    private UserService userService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private RideService rideService;

    @Operation(summary = "Show user dashboard", description = "Displays user information, upcoming and past rides.")
    @ApiResponse(responseCode = "200", description = "User dashboard loaded successfully")
    @GetMapping("/user")
    public String showUserDetails(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        User currentUser = userService.getUserByEmail(currentPrincipalName);

        model.addAttribute("user", currentUser);
        model.addAttribute("rides", rideService.getRidesByUser(currentUser));

        model.addAttribute("driversRides", rideService.getRidesByDriver(currentUser));

        model.addAttribute("pastRides",
                rideService.getPastRidesByUser(currentUser).stream()
                        .sorted(Comparator.comparing(Ride::getDepartureTime).reversed())
                        .limit(5)
                        .toList());

        model.addAttribute("waitingApprove", rideService.getRidesWaitingApprove(currentUser));
        return "user";
    }

    @Operation(summary = "Update user info", description = "Updates user profile details.")
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "Redirected after successful update"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/user")
    public String updateUser(@ModelAttribute("user") User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        User currentUser = userService.getUserByEmail(currentPrincipalName);

        currentUser.setFirstName(user.getFirstName());
        currentUser.setLastName(user.getLastName());
        currentUser.setEmail(user.getEmail());
        currentUser.setPhoneNumber(user.getPhoneNumber());
        currentUser.setCity(user.getCity());
        currentUser.setStreet(user.getStreet());
        currentUser.setPostNum(user.getPostNum());

        userService.updateUser(currentUser);
        return "redirect:/user?success";
    }

    @Operation(summary = "Upload profile picture", description = "Uploads and sets a new profile picture for the user.")
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "Redirected after successful upload"),
            @ApiResponse(responseCode = "400", description = "Invalid file or upload error")
    })
    @PostMapping("/user/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, Principal principal) {
        User currentUser = userService.getUserByEmail(principal.getName());

        String profilePicturePath = fileStorageService.storeFile(file);
        currentUser.setProfilePicturePath("/img/" + profilePicturePath);

        userService.updateUser(currentUser);

        return "redirect:/user";
    }

    @Operation(summary = "Change password", description = "Updates the user's password.")
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid password format")
    })
    @PostMapping("/user/passwd")
    public String changePasswd(@ModelAttribute("user") User user) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        User currentUser = userService.getUserByEmail(currentPrincipalName);

        if (!userService.validatePassword(user.getPassword())) {
            return "redirect:/user?errorPasswd";
        }

        currentUser.setPassword(userService.encodePasswd(user.getPassword()));



        userService.updateUser(currentUser);
        return "redirect:/user?success";
    }

    @Operation(summary = "Update driver info", description = "Updates the driver's car and identification details.")
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "Driver details updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid driver data")
    })
    @PostMapping("/driver")
    public String updateDriver(@ModelAttribute("user") User driver) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        User currentUser = userService.getUserByEmail(currentPrincipalName);

        currentUser.setCarType(driver.getCarType());
        currentUser.setLicensePlate(driver.getLicensePlate());

        if (driver.getIdPhotoPath() != null) {
            currentUser.setIdPhotoPath("/img/" + driver.getIdPhotoPath());
        }

        userService.updateUser(currentUser);
        return "redirect:/user?success";
    }
}

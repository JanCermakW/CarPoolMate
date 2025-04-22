package com.carpoolmate.carpoolmate.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class MainController {
    @Operation(summary = "Login page", description = "Displays the login form for user authentication.")
    @ApiResponse(responseCode = "200", description = "Login page displayed")
    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @Operation(summary = "Error page", description = "Displays a generic error page for failed requests.")
    @ApiResponse(responseCode = "200", description = "Error page displayed")
    @GetMapping("/error")
    public String error() {
        return "error";
    }
}

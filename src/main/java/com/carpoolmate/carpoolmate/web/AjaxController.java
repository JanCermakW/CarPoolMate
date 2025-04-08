package com.carpoolmate.carpoolmate.web;

import com.carpoolmate.carpoolmate.service.RideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
public class AjaxController {

    @Autowired
    private RideService rideService;

    @GetMapping("/suggestions")
    public List<String> getLocationSuggestions(@RequestParam("query") String query, @RequestParam String field) {
        return rideService.getLocationSuggestions(query, field);
    }
}

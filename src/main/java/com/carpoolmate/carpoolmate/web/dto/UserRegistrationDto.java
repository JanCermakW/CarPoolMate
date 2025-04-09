package com.carpoolmate.carpoolmate.web.dto;

import com.carpoolmate.carpoolmate.model.Role;
import com.carpoolmate.carpoolmate.model.RoleType;

import java.util.HashSet;
import java.util.Set;

public class UserRegistrationDto {
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    private String carType;
    private String licensePlate;
    private String driverImagePath;


    private RoleType role;

    public UserRegistrationDto(String firstName, String lastName, String email, String password) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public UserRegistrationDto() {

    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public RoleType getRole() {
        return role;
    }

    public void setRole(RoleType role) {
        this.role = role;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getDriverImagePath() {
        return driverImagePath;
    }

    public void setDriverImagePath(String driverImage) {
        this.driverImagePath = driverImage;
    }
}

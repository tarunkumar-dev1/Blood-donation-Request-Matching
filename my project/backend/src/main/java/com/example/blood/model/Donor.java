package com.example.blood.model;

import java.time.LocalDateTime;

public class Donor {
    private final String id;
    private final String name;
    private final String dob;
    private final String bloodType;
    private final String phone;
    private final String email;
    private final String address;
    private final String pincode;
    private final String location;
    private final String center;
    private final String history;
    private final String surgeries;
    private final String status;
    private final LocalDateTime createdAt;

    public Donor(String id, String name, String dob, String bloodType, String phone, String email,
                 String address, String pincode, String location, String center, String history, String surgeries, String status) {
        this.id = id;
        this.name = name;
        this.dob = dob;
        this.bloodType = bloodType;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.pincode = pincode;
        this.location = location;
        this.center = center;
        this.history = history;
        this.surgeries = surgeries;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDob() { return dob; }
    public String getBloodType() { return bloodType; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public String getLocation() { return location; }
    public String getPincode() { return pincode; }
    public String getCenter() { return center; }
    public String getHistory() { return history; }
    public String getSurgeries() { return surgeries; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

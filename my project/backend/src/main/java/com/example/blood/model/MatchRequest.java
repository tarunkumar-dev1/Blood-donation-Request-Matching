package com.example.blood.model;

import java.time.LocalDateTime;

public class MatchRequest {
    private final String id;
    private final String fullname;
    private final String email;
    private final String bloodtype;
    private final String pincode;
    private final String location;
    private final String note;
    private final String status;
    private final LocalDateTime createdAt;

    public MatchRequest(String id, String fullname, String email, String bloodtype, String pincode, String location, String note, String status) {
        this.id = id;
        this.fullname = fullname;
        this.email = email;
        this.bloodtype = bloodtype;
        this.pincode = pincode;
        this.location = location;
        this.note = note;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public String getFullname() { return fullname; }
    public String getEmail() { return email; }
    public String getBloodtype() { return bloodtype; }
    public String getLocation() { return location; }
    public String getPincode() { return pincode; }
    public String getNote() { return note; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

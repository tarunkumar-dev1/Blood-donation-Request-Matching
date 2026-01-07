package com.example.blood.model;

import java.time.LocalDateTime;

public class Match {
    private String matchId;
    private String donorId;
    private String donorName;
    private String donorPhone;
    private String donorEmail;
    private String requestId;
    private String requestName;
    private String requestEmail;
    private String requestPhone;
    private String bloodType;
    private String donorPincode;
    private String requestPincode;
    private boolean locationMatch;
    private String requestNote;
    private String status; // new, pending, completed, rejected
    private String priority; // high, medium, low
    private LocalDateTime matchedAt;
    private LocalDateTime updatedAt;

    // Constructors
    public Match() {
        this.matchedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = "new";
        this.priority = "medium";
    }

    // Getters and Setters
    public String getMatchId() { return matchId; }
    public void setMatchId(String matchId) { this.matchId = matchId; }

    public String getDonorId() { return donorId; }
    public void setDonorId(String donorId) { this.donorId = donorId; }

    public String getDonorName() { return donorName; }
    public void setDonorName(String donorName) { this.donorName = donorName; }

    public String getDonorPhone() { return donorPhone; }
    public void setDonorPhone(String donorPhone) { this.donorPhone = donorPhone; }

    public String getDonorEmail() { return donorEmail; }
    public void setDonorEmail(String donorEmail) { this.donorEmail = donorEmail; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getRequestName() { return requestName; }
    public void setRequestName(String requestName) { this.requestName = requestName; }

    public String getRequestEmail() { return requestEmail; }
    public void setRequestEmail(String requestEmail) { this.requestEmail = requestEmail; }

    public String getRequestPhone() { return requestPhone; }
    public void setRequestPhone(String requestPhone) { this.requestPhone = requestPhone; }

    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }

    public String getDonorPincode() { return donorPincode; }
    public void setDonorPincode(String donorPincode) { this.donorPincode = donorPincode; }

    public String getRequestPincode() { return requestPincode; }
    public void setRequestPincode(String requestPincode) { this.requestPincode = requestPincode; }

    public boolean isLocationMatch() { return locationMatch; }
    public void setLocationMatch(boolean locationMatch) { this.locationMatch = locationMatch; }

    public String getRequestNote() { return requestNote; }
    public void setRequestNote(String requestNote) { this.requestNote = requestNote; }

    public String getStatus() { return status; }
    public void setStatus(String status) { 
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public LocalDateTime getMatchedAt() { return matchedAt; }
    public void setMatchedAt(LocalDateTime matchedAt) { this.matchedAt = matchedAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

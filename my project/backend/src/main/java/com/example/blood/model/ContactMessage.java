package com.example.blood.model;

import java.time.LocalDateTime;

public class ContactMessage {
    private final String id;
    private final String name;
    private final String email;
    private final String message;
    private final LocalDateTime createdAt;

    public ContactMessage(String id, String name, String email, String message) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getMessage() { return message; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

package com.example.blood.model;

public class AuthUser {
    private final String phone;
    private final String password;

    public AuthUser(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }

    public String getPhone() { return phone; }
    public String getPassword() { return password; }
}

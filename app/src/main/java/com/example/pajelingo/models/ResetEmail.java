package com.example.pajelingo.models;

public class ResetEmail {
    private final String email;

    public ResetEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}

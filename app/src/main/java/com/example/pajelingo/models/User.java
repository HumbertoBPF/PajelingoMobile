package com.example.pajelingo.models;

import java.io.Serializable;

public class User implements Serializable {
    private final String email;
    private final String username;
    private final String password;

    public User(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}

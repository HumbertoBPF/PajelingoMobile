package com.example.pajelingo.models;

import java.io.Serializable;

public class User implements Serializable {
    private final String email;
    private final String username;
    private String password;
    private final String picture;

    public User(String email, String username, String password, String picture) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.picture = picture;
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

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPicture() {
        return picture;
    }
}

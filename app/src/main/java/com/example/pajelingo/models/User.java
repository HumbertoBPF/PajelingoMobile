package com.example.pajelingo.models;

import java.io.Serializable;

public class User implements Serializable {
    private String email;
    private String username;
    private String password;
    private final String picture;
    private final String bio;

    public User(String email, String username, String password, String picture, String bio) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.picture = picture;
        this.bio = bio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getBio() {
        return bio;
    }
}

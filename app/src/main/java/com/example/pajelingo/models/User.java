package com.example.pajelingo.models;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    private final String email;
    private final String username;
    private String password;
    private String picture;
    private final String bio;
    private List<Badge> badges;

    public User(String email, String username, String password, String bio) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.bio = bio;
    }

    public User(String email, String username, String password, String picture, String bio) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.picture = picture;
        this.bio = bio;
    }

    public User(String email, String username, String password, String picture, String bio, List<Badge> badges) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.picture = picture;
        this.bio = bio;
        this.badges = badges;
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

    public String getBio() {
        return bio;
    }

    public List<Badge> getBadges() {
        return badges;
    }
}

package com.example.pajelingo.models;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Badge implements Serializable {
    private final String name;
    private final String color;
    private final String description;
    private final String image;

    public Badge(String name, String color, String description, String image) {
        this.name = name;
        this.color = color;
        this.description = description;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    @NonNull
    @Override
    public String toString() {
        return this.name;
    }
}

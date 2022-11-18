package com.example.pajelingo.models.ui;

public class OnBoardingItem {
    private Integer imageResource;
    private String description;

    public OnBoardingItem(Integer image, String description) {
        this.imageResource = image;
        this.description = description;
    }

    public Integer getImageResource() {
        return imageResource;
    }

    public String getDescription() {
        return description;
    }
}

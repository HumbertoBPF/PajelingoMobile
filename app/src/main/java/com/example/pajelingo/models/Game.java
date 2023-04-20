package com.example.pajelingo.models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity
public class Game implements Serializable {

    @PrimaryKey()
    private Long id;
    @SerializedName(value = "game_name")
    private String gameName;
    @SerializedName(value = "android_game_activity")
    private String androidGameActivity;
    @Ignore
    private String image;
    @SerializedName(value = "image_uri")
    private String imageUri;
    private String instructions;

    public Game(Long id, String gameName, String androidGameActivity, String imageUri, String instructions) {
        this.id = id;
        this.gameName = gameName;
        this.androidGameActivity = androidGameActivity;
        this.imageUri = imageUri;
        this.instructions = instructions;
    }

    public Game(Long id, String gameName, String androidGameActivity, String image, String imageUri, String instructions) {
        this.id = id;
        this.gameName = gameName;
        this.androidGameActivity = androidGameActivity;
        this.image = image;
        this.imageUri = imageUri;
        this.instructions = instructions;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getAndroidGameActivity() {
        return androidGameActivity;
    }

    public void setAndroidGameActivity(String androidGameActivity) {
        this.androidGameActivity = androidGameActivity;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
}

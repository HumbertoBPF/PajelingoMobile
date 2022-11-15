package com.example.pajelingo.models;

import androidx.room.Entity;
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

    public Game(Long id, String gameName, String androidGameActivity) {
        this.id = id;
        this.gameName = gameName;
        this.androidGameActivity = androidGameActivity;
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
}

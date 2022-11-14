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

    public Game(Long id, String gameName) {
        this.id = id;
        this.gameName = gameName;
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
}

package com.example.pajelingo.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Score {
    @PrimaryKey()
    private Long id;
    private String user;
    private String language;
    private Long game;
    private Long score;

    public Score(String user, String language, Long game, Long score) {
        this.user = user;
        this.language = language;
        this.game = game;
        this.score = score;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Long getGame() {
        return game;
    }

    public void setGame(Long game) {
        this.game = game;
    }

    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }
}

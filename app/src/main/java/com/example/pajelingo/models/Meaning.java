package com.example.pajelingo.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class Meaning {

    @PrimaryKey()
    private Long id;
    @SerializedName(value = "word")
    private Long idWord;
    private String meaning;

    public Meaning(Long idWord, String meaning) {
        this.idWord = idWord;
        this.meaning = meaning;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdWord() {
        return idWord;
    }

    public void setIdWord(Long idWord) {
        this.idWord = idWord;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }
}

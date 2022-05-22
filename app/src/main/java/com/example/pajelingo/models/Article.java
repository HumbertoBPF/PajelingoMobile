package com.example.pajelingo.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class Article {
    @PrimaryKey(autoGenerate = true)
    private Long id;
    @SerializedName(value = "article_name")
    private String articleName;
    @SerializedName(value = "language")
    private Long idLanguage;

    public Article(Long idLanguage) {
        this.idLanguage = idLanguage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getArticleName() {
        return articleName;
    }

    public void setArticleName(String articleName) {
        this.articleName = articleName;
    }

    public Long getIdLanguage() {
        return idLanguage;
    }

    public void setIdLanguage(Long idLanguage) {
        this.idLanguage = idLanguage;
    }
}

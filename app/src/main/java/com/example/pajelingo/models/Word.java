package com.example.pajelingo.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

@Entity
public class Word implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private Long id;
    @SerializedName(value = "word_name")
    private String wordName;
    @SerializedName(value = "language")
    private String language;
    @SerializedName(value = "article")
    private Long idArticle;
    @SerializedName(value = "category")
    private String category;
    @SerializedName(value = "synonyms")
    private List<Long> idsSynonyms;

    public Word(String wordName, String language, Long idArticle, String category, List<Long> idsSynonyms) {
        this.wordName = wordName;
        this.language = language;
        this.idArticle = idArticle;
        this.category = category;
        this.idsSynonyms = idsSynonyms;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWordName() {
        return wordName;
    }

    public void setWordName(String wordName) {
        this.wordName = wordName;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Long getIdArticle() {
        return idArticle;
    }

    public void setIdArticle(Long idArticle) {
        this.idArticle = idArticle;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<Long> getIdsSynonyms() {
        return idsSynonyms;
    }

    public void setIdsSynonyms(List<Long> idsSynonyms) {
        this.idsSynonyms = idsSynonyms;
    }
}

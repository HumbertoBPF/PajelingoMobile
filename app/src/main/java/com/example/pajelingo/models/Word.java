package com.example.pajelingo.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@Entity
public class Word {
    @PrimaryKey(autoGenerate = true)
    private Long id;
    @SerializedName(value = "word_name")
    private String wordName;
    @SerializedName(value = "language")
    private Long idLanguage;
    @SerializedName(value = "article")
    private Long idArticle;
    @SerializedName(value = "category")
    private Long idCategory;
    @SerializedName(value = "synonyms")
    private List<Long> idsSynonyms;

    public Word(String wordName, Long idLanguage, Long idArticle, Long idCategory, List<Long> idsSynonyms) {
        this.wordName = wordName;
        this.idLanguage = idLanguage;
        this.idArticle = idArticle;
        this.idCategory = idCategory;
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

    public Long getIdLanguage() {
        return idLanguage;
    }

    public void setIdLanguage(Long idLanguage) {
        this.idLanguage = idLanguage;
    }

    public Long getIdArticle() {
        return idArticle;
    }

    public void setIdArticle(Long idArticle) {
        this.idArticle = idArticle;
    }

    public Long getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(Long idCategory) {
        this.idCategory = idCategory;
    }

    public List<Long> getIdsSynonyms() {
        return idsSynonyms;
    }

    public void setIdsSynonyms(List<Long> idsSynonyms) {
        this.idsSynonyms = idsSynonyms;
    }
}

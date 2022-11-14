package com.example.pajelingo.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class Conjugation {
    @PrimaryKey()
    private Long id;
    @SerializedName(value = "word")
    private Long wordId;
    @SerializedName(value = "conjugation_1")
    private String conjugation1;
    @SerializedName(value = "conjugation_2")
    private String conjugation2;
    @SerializedName(value = "conjugation_3")
    private String conjugation3;
    @SerializedName(value = "conjugation_4")
    private String conjugation4;
    @SerializedName(value = "conjugation_5")
    private String conjugation5;
    @SerializedName(value = "conjugation_6")
    private String conjugation6;
    private String tense;

    public Conjugation(Long wordId, String conjugation1, String conjugation2, String conjugation3,
                       String conjugation4, String conjugation5, String conjugation6, String tense) {
        this.wordId = wordId;
        this.conjugation1 = conjugation1;
        this.conjugation2 = conjugation2;
        this.conjugation3 = conjugation3;
        this.conjugation4 = conjugation4;
        this.conjugation5 = conjugation5;
        this.conjugation6 = conjugation6;
        this.tense = tense;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWordId() {
        return wordId;
    }

    public void setWordId(Long wordId) {
        this.wordId = wordId;
    }

    public String getConjugation1() {
        return conjugation1;
    }

    public void setConjugation1(String conjugation1) {
        this.conjugation1 = conjugation1;
    }

    public String getConjugation2() {
        return conjugation2;
    }

    public void setConjugation2(String conjugation2) {
        this.conjugation2 = conjugation2;
    }

    public String getConjugation3() {
        return conjugation3;
    }

    public void setConjugation3(String conjugation3) {
        this.conjugation3 = conjugation3;
    }

    public String getConjugation4() {
        return conjugation4;
    }

    public void setConjugation4(String conjugation4) {
        this.conjugation4 = conjugation4;
    }

    public String getConjugation5() {
        return conjugation5;
    }

    public void setConjugation5(String conjugation5) {
        this.conjugation5 = conjugation5;
    }

    public String getConjugation6() {
        return conjugation6;
    }

    public void setConjugation6(String conjugation6) {
        this.conjugation6 = conjugation6;
    }

    public String getTense() {
        return tense;
    }

    public void setTense(String tense) {
        this.tense = tense;
    }
}

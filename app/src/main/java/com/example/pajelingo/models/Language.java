package com.example.pajelingo.models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

@Entity
public class Language {

    @PrimaryKey()
    private Long id;
    @SerializedName(value = "language_name")
    private String languageName;
    @SerializedName(value = "personal_pronoun_1")
    private String personalPronoun1;
    @SerializedName(value = "personal_pronoun_2")
    private String personalPronoun2;
    @SerializedName(value = "personal_pronoun_3")
    private String personalPronoun3;
    @SerializedName(value = "personal_pronoun_4")
    private String personalPronoun4;
    @SerializedName(value = "personal_pronoun_5")
    private String personalPronoun5;
    @SerializedName(value = "personal_pronoun_6")
    private String personalPronoun6;
    @Ignore
    @SerializedName(value = "flag_image")
    private String flagImage;
    @SerializedName(value = "flag_image_uri")
    private String flagImageUri;

    public Language(String languageName) {
        this.languageName = languageName;
    }

    public Language(String languageName, String personalPronoun1, String personalPronoun2,
                    String personalPronoun3, String personalPronoun4, String personalPronoun5,
                    String personalPronoun6, String flagImage, String flagImageUri) {
        this.languageName = languageName;
        this.personalPronoun1 = personalPronoun1;
        this.personalPronoun2 = personalPronoun2;
        this.personalPronoun3 = personalPronoun3;
        this.personalPronoun4 = personalPronoun4;
        this.personalPronoun5 = personalPronoun5;
        this.personalPronoun6 = personalPronoun6;
        this.flagImage = flagImage;
        this.flagImageUri = flagImageUri;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public String getPersonalPronoun1() {
        return personalPronoun1;
    }

    public void setPersonalPronoun1(String personalPronoun1) {
        this.personalPronoun1 = personalPronoun1;
    }

    public String getPersonalPronoun2() {
        return personalPronoun2;
    }

    public void setPersonalPronoun2(String personalPronoun2) {
        this.personalPronoun2 = personalPronoun2;
    }

    public String getPersonalPronoun3() {
        return personalPronoun3;
    }

    public void setPersonalPronoun3(String personalPronoun3) {
        this.personalPronoun3 = personalPronoun3;
    }

    public String getPersonalPronoun4() {
        return personalPronoun4;
    }

    public void setPersonalPronoun4(String personalPronoun4) {
        this.personalPronoun4 = personalPronoun4;
    }

    public String getPersonalPronoun5() {
        return personalPronoun5;
    }

    public void setPersonalPronoun5(String personalPronoun5) {
        this.personalPronoun5 = personalPronoun5;
    }

    public String getPersonalPronoun6() {
        return personalPronoun6;
    }

    public void setPersonalPronoun6(String personalPronoun6) {
        this.personalPronoun6 = personalPronoun6;
    }

    public String getFlagImage() {
        return flagImage;
    }

    public void setFlagImage(String flagImage) {
        this.flagImage = flagImage;
    }

    public String getFlagImageUri() {
        return flagImageUri;
    }

    public void setFlagImageUri(String flagImageUri) {
        this.flagImageUri = flagImageUri;
    }

    @Override
    public String toString() {
        return languageName;
    }

    @Override
    public boolean equals(Object o) {
        Language language = (Language) o;

        return Objects.equals(language.getId(), this.id) &&
                Objects.equals(language.getLanguageName(), this.languageName) &&
                Objects.equals(language.getPersonalPronoun1(), this.personalPronoun1) &&
                Objects.equals(language.getPersonalPronoun2(), this.personalPronoun2) &&
                Objects.equals(language.getPersonalPronoun3(), this.personalPronoun3) &&
                Objects.equals(language.getPersonalPronoun4(), this.personalPronoun4) &&
                Objects.equals(language.getPersonalPronoun5(), this.personalPronoun5) &&
                Objects.equals(language.getPersonalPronoun6(), this.personalPronoun6);
    }
}

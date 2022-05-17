package com.example.pajelingo.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Language {

    @PrimaryKey(autoGenerate = true)
    private Long id;
    private String languageName;
    private String personalPronoun1;
    private String personalPronoun2;
    private String personalPronoun3;
    private String personalPronoun4;
    private String personalPronoun5;
    private String personalPronoun6;

    public Language(String languageName, String personalPronoun1, String personalPronoun2, String personalPronoun3,
                    String personalPronoun4, String personalPronoun5, String personalPronoun6) {
        this.languageName = languageName;
        this.personalPronoun1 = personalPronoun1;
        this.personalPronoun2 = personalPronoun2;
        this.personalPronoun3 = personalPronoun3;
        this.personalPronoun4 = personalPronoun4;
        this.personalPronoun5 = personalPronoun5;
        this.personalPronoun6 = personalPronoun6;
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
}

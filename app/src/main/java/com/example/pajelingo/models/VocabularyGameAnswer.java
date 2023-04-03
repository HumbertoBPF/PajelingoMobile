package com.example.pajelingo.models;

import com.google.gson.annotations.SerializedName;

public class VocabularyGameAnswer {
    @SerializedName(value = "word_id")
    private Long wordId;
    @SerializedName(value = "base_language")
    private String baseLanguage;
    private String answer;

    public VocabularyGameAnswer(Long wordId, String baseLanguage, String answer) {
        this.wordId = wordId;
        this.baseLanguage = baseLanguage;
        this.answer = answer;
    }

    public Long getWordId() {
        return wordId;
    }

    public void setWordId(Long wordId) {
        this.wordId = wordId;
    }

    public String getBaseLanguage() {
        return baseLanguage;
    }

    public void setBaseLanguage(String baseLanguage) {
        this.baseLanguage = baseLanguage;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}

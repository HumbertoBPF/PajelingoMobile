package com.example.pajelingo.models;

import com.google.gson.annotations.SerializedName;

public class ArticleGameAnswer {
    @SerializedName(value = "word_id")
    private Long wordId;
    private String answer;

    public ArticleGameAnswer(Long wordId, String answer) {
        this.wordId = wordId;
        this.answer = answer;
    }

    public Long getWordId() {
        return wordId;
    }

    public void setWordId(Long wordId) {
        this.wordId = wordId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}

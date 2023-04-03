package com.example.pajelingo.models;

import com.google.gson.annotations.SerializedName;

public class GameAnswerFeedback {
    private Boolean result;

    @SerializedName(value = "correct_answer")
    private String correctAnswer;
    private Long score;

    public GameAnswerFeedback(Boolean result, String correctAnswer, Long score) {
        this.result = result;
        this.correctAnswer = correctAnswer;
        this.score = score;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }
}

package com.example.pajelingo.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GameAnswerFeedback {
    private final Boolean result;

    @SerializedName(value = "correct_answer")
    private final String correctAnswer;
    private final Long score;
    @SerializedName(value = "new_badges")
    private final List<Badge> newBadges;

    public GameAnswerFeedback(Boolean result, String correctAnswer, Long score, List<Badge> newBadges) {
        this.result = result;
        this.correctAnswer = correctAnswer;
        this.score = score;
        this.newBadges = newBadges;
    }

    public Boolean getResult() {
        return result;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public Long getScore() {
        return score;
    }

    public List<Badge> getNewBadges() {
        return newBadges;
    }
}

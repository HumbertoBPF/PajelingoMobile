package com.example.pajelingo.models;

public class GameRoundWord {
    private Long id;
    private String word;
    private String tense;

    public GameRoundWord(Long id, String word, String tense) {
        this.id = id;
        this.word = word;
        this.tense = tense;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getTense() {
        return tense;
    }

    public void setTense(String tense) {
        this.tense = tense;
    }
}

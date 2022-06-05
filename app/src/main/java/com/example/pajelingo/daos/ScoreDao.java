package com.example.pajelingo.daos;

import androidx.room.Dao;

import com.example.pajelingo.models.Score;

@Dao
public abstract class ScoreDao extends BaseDao<Score> {
    public ScoreDao() {
        super("Score");
    }
}

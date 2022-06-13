package com.example.pajelingo.daos;

import androidx.room.Dao;

import com.example.pajelingo.models.Game;

@Dao
public abstract class GameDao extends BaseDao<Game>{
    public GameDao() {
        super("Game");
    }
}

package com.example.pajelingo.daos;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.pajelingo.interfaces.OnResultListener;
import com.example.pajelingo.models.Game;
import com.example.pajelingo.utils.BackgroundTask;

import java.util.List;

@Dao
public abstract class GameDao extends BaseDao<Game>{
    public GameDao() {
        super("Game");
    }

    @Query("SELECT * FROM Game WHERE (androidGameActivity IS NOT NULL) AND (LENGTH(androidGameActivity) > 0);")
    protected abstract List<Game> getEnabledGames();

    @Query("SELECT * FROM Game WHERE gameName = :gameName LIMIT 1")
    public abstract Game getGameByName(String gameName);

    public void getEnabledGames(OnResultListener<List<Game>> onResultListener){
        BackgroundTask<List<Game>> backgroundTask = new BackgroundTask<>(this::getEnabledGames, onResultListener);
        backgroundTask.execute();
    }
}

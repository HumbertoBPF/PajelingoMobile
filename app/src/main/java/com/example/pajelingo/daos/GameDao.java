package com.example.pajelingo.daos;

import android.os.AsyncTask;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.pajelingo.interfaces.OnResultListener;
import com.example.pajelingo.models.Game;

@Dao
public abstract class GameDao extends BaseDao<Game>{
    public GameDao() {
        super("Game");
    }

    @Query("SELECT * FROM Game WHERE gameName = :gameName LIMIT 1")
    public abstract Game getGameByName(String gameName);

    public AsyncTask<Void, Void, Game> getGameByNameTask(String gameName,
                                                                      OnResultListener<Game> onResultListener){
        return new AsyncTask<Void, Void, Game>() {
            @Override
            protected Game doInBackground(Void... voids) {
                return getGameByName(gameName);
            }

            @Override
            protected void onPostExecute(Game conjugations) {
                super.onPostExecute(conjugations);
                onResultListener.onResult(conjugations);
            }
        };
    }
}

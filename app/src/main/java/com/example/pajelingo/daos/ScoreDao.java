package com.example.pajelingo.daos;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.pajelingo.interfaces.OnResultListener;
import com.example.pajelingo.models.Score;
import com.example.pajelingo.utils.BackgroundTask;

import java.util.List;

@Dao
public abstract class ScoreDao extends BaseDao<Score> {
    public ScoreDao() {
        super("Score");
    }
    // Perform a group by to compute the total score
    @Query("SELECT id, user, language, game, sum(score) AS score FROM Score WHERE language=:languageName GROUP BY user ORDER BY score DESC;")
    public abstract List<Score> getTotalScoresByLanguage(String languageName);

    @Query("SELECT id, user, language, game, score AS score FROM Score WHERE (language=:languageName) AND (user=:username) ORDER BY game ASC;")
    public abstract List<Score> getScoresByUserAndByLanguage(String username, String languageName);

    public void getTotalScoresByLanguage(String languageName, OnResultListener<List<Score>> onResultListener){
        BackgroundTask<List<Score>> backgroundTask = new BackgroundTask<>(() -> getTotalScoresByLanguage(languageName), onResultListener);
        backgroundTask.execute();
    }

    public void getScoresByUserAndByLanguage(String username, String languageName, OnResultListener<List<Score>> onResultListener){
        BackgroundTask<List<Score>> backgroundTask = new BackgroundTask<>(() -> getScoresByUserAndByLanguage(username, languageName), onResultListener);
        backgroundTask.execute();
    }
}

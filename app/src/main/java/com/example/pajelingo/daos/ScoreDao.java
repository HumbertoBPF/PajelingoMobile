package com.example.pajelingo.daos;

import android.os.AsyncTask;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.pajelingo.interfaces.OnResultListener;
import com.example.pajelingo.models.Score;

import java.util.List;

@Dao
public abstract class ScoreDao extends BaseDao<Score> {
    public ScoreDao() {
        super("Score");
    }
    // Perform a group by to compute the total score
    @Query("SELECT id, user, language, game, sum(score) AS score FROM Score GROUP BY user ORDER BY score DESC;")
    protected abstract List<Score> getAllScoresSorted();

    @Query("SELECT id, user, language, game, sum(score) AS score FROM Score WHERE language=:languageName GROUP BY user ORDER BY score DESC;")
    protected abstract List<Score> getAllScoresSorted(String languageName);

    public AsyncTask<Void,Void,List<Score>> getAllScoresSortedTask(OnResultListener<List<Score>> onResultListener){
        return new AsyncTask<Void, Void, List<Score>>() {
            @Override
            protected List<Score> doInBackground(Void... voids) {
                return getAllScoresSorted();
            }

            @Override
            protected void onPostExecute(List<Score> scores) {
                super.onPostExecute(scores);
                onResultListener.onResult(scores);
            }
        };
    }

    public AsyncTask<Void,Void,List<Score>> getAllScoresSortedTask(String languageName,
                                                                   OnResultListener<List<Score>> onResultListener){
        return new AsyncTask<Void, Void, List<Score>>() {
            @Override
            protected List<Score> doInBackground(Void... voids) {
                return getAllScoresSorted(languageName);
            }

            @Override
            protected void onPostExecute(List<Score> scores) {
                super.onPostExecute(scores);
                onResultListener.onResult(scores);
            }
        };
    }
}

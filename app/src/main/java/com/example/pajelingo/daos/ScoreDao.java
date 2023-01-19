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
    @Query("SELECT id, user, language, game, sum(score) AS score FROM Score WHERE language=:languageName GROUP BY user ORDER BY score DESC;")
    public abstract List<Score> getTotalScoresByLanguage(String languageName);

    @Query("SELECT id, user, language, game, score AS score FROM Score WHERE (language=:languageName) AND (user=:username) ORDER BY game ASC;")
    public abstract List<Score> getScoresByUserAndByLanguage(String username, String languageName);

    public AsyncTask<Void,Void,List<Score>> getTotalScoresByLanguage(String languageName,
                                                                     OnResultListener<List<Score>> onResultListener){
        return new AsyncTask<Void, Void, List<Score>>() {
            @Override
            protected List<Score> doInBackground(Void... voids) {
                return getTotalScoresByLanguage(languageName);
            }

            @Override
            protected void onPostExecute(List<Score> scores) {
                super.onPostExecute(scores);
                onResultListener.onResult(scores);
            }
        };
    }

    public AsyncTask<Void,Void,List<Score>> getScoresByUserAndByLanguage(String username, String languageName,
                                                                         OnResultListener<List<Score>> onResultListener){
        return new AsyncTask<Void, Void, List<Score>>() {
            @Override
            protected List<Score> doInBackground(Void... voids) {
                return getScoresByUserAndByLanguage(username, languageName);
            }

            @Override
            protected void onPostExecute(List<Score> scores) {
                super.onPostExecute(scores);
                onResultListener.onResult(scores);
            }
        };
    }
}

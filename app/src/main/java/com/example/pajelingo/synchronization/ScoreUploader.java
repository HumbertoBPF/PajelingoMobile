package com.example.pajelingo.synchronization;

import static android.content.Context.MODE_PRIVATE;
import static com.example.pajelingo.util.Tools.getAuthToken;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.pajelingo.R;
import com.example.pajelingo.models.Language;
import com.example.pajelingo.models.Score;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScoreUploader {

    private final Context context;
    private final Language language;
    private final String game;

    public ScoreUploader(Context context, Language language, String game) {
        this.context = context;
        this.language = language;
        this.game = game;
    }

    public void upload(){
        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.sp_file_name), MODE_PRIVATE);

        String username = sp.getString(context.getString(R.string.username_sp), null);
        String password = sp.getString(context.getString(R.string.password_sp), null);

        Call<List<Score>> call = LanguageSchoolAPIHelper.getApiObject().getScore(getAuthToken(username, password),
                language.getId(), game);
        call.enqueue(new Callback<List<Score>>() {
            @Override
            public void onResponse(Call<List<Score>> call, Response<List<Score>> response) {
                if (response.isSuccessful()){
                    List<Score> scores = response.body();
                    if (scores == null || scores.isEmpty()){
                        Score newScore = new Score(null, language.getLanguageName(), game, 1L);
                        Call<Score> createScoreCall = LanguageSchoolAPIHelper
                                .getApiObject().createScore(getAuthToken(username, password), newScore);
                        createScoreCall.enqueue(new Callback<Score>() {
                            @Override
                            public void onResponse(Call<Score> call, Response<Score> response) {
                                if (response.isSuccessful()) {
                                    Score score = response.body();
                                    if (score != null){
                                        Toast.makeText(context, "Your current score is "+score.getScore(), Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    Toast.makeText(context, "Fail to update your score data.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Score> call, Throwable t) {
                                Toast.makeText(context, "Fail to update your score data.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else{
                        Call<Score> incrementScoreCall = LanguageSchoolAPIHelper
                                .getApiObject().incrementScore(getAuthToken(username, password), scores.get(0).getId());
                        incrementScoreCall.enqueue(new Callback<Score>() {
                            @Override
                            public void onResponse(Call<Score> call, Response<Score> response) {
                                if (response.isSuccessful()) {
                                    Score score = response.body();
                                    if (score != null){
                                        Toast.makeText(context, "Your current score is "+score.getScore(), Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    Toast.makeText(context, "Fail to update your score data.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Score> call, Throwable t) {
                                Toast.makeText(context, "Fail to update your score data.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Score>> call, Throwable t) {
                Toast.makeText(context, "Fail to update your score data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}

package com.example.pajelingo.retrofit_calls;

import androidx.annotation.NonNull;

import com.example.pajelingo.interfaces.HttpResponseInterface;
import com.example.pajelingo.models.GameRoundWord;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameRoundCall extends IdlingResource {
    public void execute(Call<GameRoundWord> call, HttpResponseInterface<GameRoundWord> httpResponseInterface) {
        incrementIdlingResource();

        call.enqueue(new Callback<GameRoundWord>() {
            @Override
            public void onResponse(@NonNull Call<GameRoundWord> call, @NonNull Response<GameRoundWord> response) {
                GameRoundWord gameRoundWord = response.body();

                if ((response.isSuccessful()) && (gameRoundWord != null)) {
                    httpResponseInterface.onSuccess(gameRoundWord);
                }else {
                    httpResponseInterface.onError(response);
                }

                decrementIdlingResource();
            }

            @Override
            public void onFailure(@NonNull Call<GameRoundWord> call, @NonNull Throwable t) {
                httpResponseInterface.onFailure();
                decrementIdlingResource();
            }
        });
    }
}

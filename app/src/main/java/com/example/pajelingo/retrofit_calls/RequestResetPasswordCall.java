package com.example.pajelingo.retrofit_calls;

import android.os.Handler;

import androidx.annotation.NonNull;

import com.example.pajelingo.interfaces.HttpResponseInterface;
import com.example.pajelingo.models.ResetEmail;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestResetPasswordCall extends IdlingResource {
    private final Handler handler = new Handler();

    public void execute(String email, HttpResponseInterface<Void> httpResponseInterface) {
        Call<Void> call = LanguageSchoolAPIHelper.getApiObject().resetAccount(new ResetEmail(email));

        incrementIdlingResource();

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                handler.postDelayed(() -> {
                    if (response.isSuccessful()) {
                        httpResponseInterface.onSuccess(response.body());
                    }else{
                        httpResponseInterface.onError(response);
                    }

                    decrementIdlingResource();
                }, 2000);
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                handler.postDelayed(() -> {
                    httpResponseInterface.onFailure();
                    decrementIdlingResource();
                }, 2000);
            }
        });
    }
}

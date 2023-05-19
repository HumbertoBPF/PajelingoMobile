package com.example.pajelingo.retrofit_calls;

import androidx.annotation.NonNull;

import com.example.pajelingo.interfaces.HttpResponseInterface;
import com.example.pajelingo.models.User;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchAccountCall extends IdlingResource {
    public void execute(String username, HttpResponseInterface<User> httpResponseInterface) {
        incrementIdlingResource();

        Call<User> call = LanguageSchoolAPIHelper.getApiObject().getAccount(username);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                User responseUser = response.body();

                if (response.isSuccessful() && (responseUser != null)) {
                    httpResponseInterface.onSuccess(responseUser);
                } else {
                    httpResponseInterface.onError(response);
                }

                decrementIdlingResource();
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                httpResponseInterface.onFailure();
                decrementIdlingResource();
            }
        });
    }
}

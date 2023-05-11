package com.example.pajelingo.retrofit_calls;

import androidx.annotation.NonNull;

import com.example.pajelingo.interfaces.HttpResponseInterface;
import com.example.pajelingo.models.User;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupCall extends IdlingResource{
    public void execute(String email, String username, String password, HttpResponseInterface<User> httpResponseInterface) {
        incrementIdlingResource();

        Call<User> call = LanguageSchoolAPIHelper.getApiObject().signup(new User(email, username, password, null));

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.code() == 201) {
                    httpResponseInterface.onSuccess(response.body());
                }else if (response.code() == 400) {
                    httpResponseInterface.onError(response);
                }else{
                    httpResponseInterface.onFailure();
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

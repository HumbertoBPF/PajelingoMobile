package com.example.pajelingo.retrofit_calls;

import static com.example.pajelingo.utils.SharedPreferences.getAuthToken;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.pajelingo.interfaces.HttpResponseInterface;
import com.example.pajelingo.models.User;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProfileCall extends IdlingResource{
    private final Context context;

    public UpdateProfileCall(Context context) {
        this.context = context;
    }

    public void execute(User user, HttpResponseInterface<User> httpResponseInterface) {
        incrementIdlingResource();

        Call<User> call = LanguageSchoolAPIHelper.getApiObject().updateAccount(getAuthToken(context), user);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                User user = response.body();
                if ((response.code() == 200) && (user != null)) {
                    httpResponseInterface.onSuccess(user);
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

package com.example.pajelingo.retrofit_calls;

import static com.example.pajelingo.utils.SharedPreferences.getAuthToken;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.pajelingo.interfaces.HttpResponseInterface;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountDeletionCall extends IdlingResource{
    private final Context context;

    public AccountDeletionCall(Context context) {
        this.context = context;
    }

    public void execute(HttpResponseInterface<Void> httpResponseInterface) {
        incrementIdlingResource();

        Call<Void> call = LanguageSchoolAPIHelper.getApiObject().deleteAccount(getAuthToken(context));

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()){
                    httpResponseInterface.onSuccess(response.body());
                }else{
                    httpResponseInterface.onError(response);
                }
                decrementIdlingResource();
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                httpResponseInterface.onFailure();
                decrementIdlingResource();
            }
        });
    }
}

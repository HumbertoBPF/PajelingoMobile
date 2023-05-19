package com.example.pajelingo.retrofit_calls;

import androidx.annotation.NonNull;

import com.example.pajelingo.interfaces.HttpResponseInterface;
import com.example.pajelingo.models.Page;
import com.example.pajelingo.models.User;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchAccountsCall extends IdlingResource {
    public void execute(String q, int index, HttpResponseInterface<Page<User>> httpResponseInterface) {
        incrementIdlingResource();

        Call<Page<User>> accountsCall = LanguageSchoolAPIHelper.getApiObject().getAccounts(q, index);

        accountsCall.enqueue(new Callback<Page<User>>() {
            @Override
            public void onResponse(@NonNull Call<Page<User>> call, @NonNull Response<Page<User>> response) {
                Page<User> returnedPage = response.body();

                if ((response.isSuccessful()) && (returnedPage != null)) {
                    httpResponseInterface.onSuccess(returnedPage);
                } else {
                    httpResponseInterface.onError(response);
                }
                decrementIdlingResource();
            }

            @Override
            public void onFailure(@NonNull Call<Page<User>> call, @NonNull Throwable t) {
                httpResponseInterface.onFailure();
                decrementIdlingResource();
            }
        });
    }
}

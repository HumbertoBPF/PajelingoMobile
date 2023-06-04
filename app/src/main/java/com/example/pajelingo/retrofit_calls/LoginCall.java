package com.example.pajelingo.retrofit_calls;

import static com.example.pajelingo.utils.SharedPreferences.saveToken;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.pajelingo.interfaces.HttpResponseInterface;
import com.example.pajelingo.models.Token;
import com.example.pajelingo.models.User;
import com.example.pajelingo.retrofit.LanguageSchoolAPI;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginCall extends IdlingResource implements HttpResponseInterface<User>{
    private final Context context;
    private final LanguageSchoolAPI languageSchoolAPI;
    private HttpResponseInterface<User> httpResponseInterface;

    public LoginCall(Context context) {
        this.context = context;
        this.languageSchoolAPI = LanguageSchoolAPIHelper.getApiObject();
    }

    public void execute(String username, String password, HttpResponseInterface<User> httpResponseInterface) {
        this.httpResponseInterface = httpResponseInterface;
        login(username, password);
    }

    private void login(String username, String password){
        incrementIdlingResource();

        Call<Token> tokenCall = languageSchoolAPI.getToken(new User("", username, password, ""));

        tokenCall.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(@NonNull Call<Token> call, @NonNull Response<Token> response) {
                Token token = response.body();
                if ((response.isSuccessful()) && (token != null)) {
                    saveToken(context, token);

                    UserDataCall userDataCall = new UserDataCall(context);
                    userDataCall.execute(LoginCall.this);
                }else {
                    if (response.code() == 400) {
                        onError(null);
                    } else {
                        LoginCall.this.onFailure();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Token> call, @NonNull Throwable t) {
                LoginCall.this.onFailure();
            }
        });


    }

    @Override
    public void onSuccess(User user) {
        httpResponseInterface.onSuccess(user);
        decrementIdlingResource();
    }

    @Override
    public void onError(Response<User> response) {
        httpResponseInterface.onError(response);
        decrementIdlingResource();
    }

    @Override
    public void onFailure() {
        httpResponseInterface.onFailure();
        decrementIdlingResource();
    }
}

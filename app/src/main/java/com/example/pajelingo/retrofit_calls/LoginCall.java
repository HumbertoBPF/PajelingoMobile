package com.example.pajelingo.retrofit_calls;

import static com.example.pajelingo.utils.SharedPreferences.getAuthToken;
import static com.example.pajelingo.utils.SharedPreferences.saveToken;
import static com.example.pajelingo.utils.SharedPreferences.saveUserData;

import android.content.Context;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.example.pajelingo.daos.WordDao;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.interfaces.HttpResponseInterface;
import com.example.pajelingo.models.Token;
import com.example.pajelingo.models.User;
import com.example.pajelingo.models.Word;
import com.example.pajelingo.retrofit.LanguageSchoolAPI;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginCall extends IdlingResource{
    private final Context context;
    private final LanguageSchoolAPI languageSchoolAPI;
    private final WordDao wordDao;
    private HttpResponseInterface<User> httpResponseInterface;

    public LoginCall(Context context) {
        this.context = context;
        this.languageSchoolAPI = LanguageSchoolAPIHelper.getApiObject();
        this.wordDao = AppDatabase.getInstance(context).getWordDao();
    }

    public void execute(String username, String password, HttpResponseInterface<User> httpResponseInterface) {
        this.httpResponseInterface = httpResponseInterface;
        login(username, password);
    }

    private void login(String username, String password){
        incrementIdlingResource();

        Call<Token> tokenCall = languageSchoolAPI.getToken(new User("", username, password, null));

        tokenCall.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(@NonNull Call<Token> call, @NonNull Response<Token> response) {
                Token token = response.body();
                if ((response.isSuccessful()) && (token != null)) {
                    saveToken(context, token);
                    getUserData();
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

    private void getUserData() {
        Call<User> call = languageSchoolAPI.login(getAuthToken(context));
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                User user = response.body();
                if ((response.isSuccessful()) && (user != null)){
                    getWords(user);
                }else {
                    if (response.code() == 401) {
                        onError(response);
                    } else {
                        LoginCall.this.onFailure();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                LoginCall.this.onFailure();
            }
        });
    }

    private void getWords(User user) {
        Call<List<Word>> call = languageSchoolAPI.getWords(getAuthToken(context));
        call.enqueue(new Callback<List<Word>>() {
            @Override
            public void onResponse(@NonNull Call<List<Word>> call, @NonNull Response<List<Word>> response) {
                List<Word> words = response.body();
                if ((response.isSuccessful()) && (words != null)){
                    wordDao.save(words, result -> new Handler().postDelayed(() -> {
                        saveUserData(context, user);
                        httpResponseInterface.onSuccess(user);
                        decrementIdlingResource();
                    }, 2000));
                }else {
                    if (response.code() == 401) {
                        onError(null);
                    } else {
                        LoginCall.this.onFailure();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Word>> call, @NonNull Throwable t) {
                LoginCall.this.onFailure();
            }
        });
    }

    public void onError(Response<User> response) {
        httpResponseInterface.onError(response);
        decrementIdlingResource();
    }

    public void onFailure() {
        httpResponseInterface.onFailure();
        decrementIdlingResource();
    }
}

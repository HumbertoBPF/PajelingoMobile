package com.example.pajelingo.retrofit_calls;

import static com.example.pajelingo.utils.SharedPreferences.getAuthToken;
import static com.example.pajelingo.utils.SharedPreferences.saveUserData;

import android.content.Context;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.example.pajelingo.daos.WordDao;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.interfaces.HttpResponseInterface;
import com.example.pajelingo.models.User;
import com.example.pajelingo.models.Word;
import com.example.pajelingo.retrofit.LanguageSchoolAPI;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDataCall extends IdlingResource {
    private final Context context;
    private final LanguageSchoolAPI languageSchoolAPI;
    private final WordDao wordDao;
    private HttpResponseInterface<User> httpResponseInterface;

    public UserDataCall(Context context) {
        this.context = context;
        this.languageSchoolAPI = LanguageSchoolAPIHelper.getApiObject();
        this.wordDao = AppDatabase.getInstance(context).getWordDao();
    }

    public void execute(HttpResponseInterface<User> httpResponseInterface) {
        incrementIdlingResource();
        this.httpResponseInterface = httpResponseInterface;
        getUserData();
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
                        UserDataCall.this.onFailure();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                UserDataCall.this.onFailure();
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
                    onSuccess(words, user);
                }else {
                    if (response.code() == 401) {
                        onError(null);
                    } else {
                        UserDataCall.this.onFailure();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Word>> call, @NonNull Throwable t) {
                UserDataCall.this.onFailure();
            }
        });
    }

    private void onSuccess(List<Word> words, User user) {
        wordDao.save(words, result -> new Handler().postDelayed(() -> {
            saveUserData(context, user);
            httpResponseInterface.onSuccess(user);
            decrementIdlingResource();
        }, 2000));
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

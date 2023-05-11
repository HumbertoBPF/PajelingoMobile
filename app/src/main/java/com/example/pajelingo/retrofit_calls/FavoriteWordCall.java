package com.example.pajelingo.retrofit_calls;

import static com.example.pajelingo.utils.SharedPreferences.getAuthToken;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.pajelingo.daos.WordDao;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.interfaces.HttpResponseInterface;
import com.example.pajelingo.models.FavoriteWordPayload;
import com.example.pajelingo.models.Word;
import com.example.pajelingo.retrofit.LanguageSchoolAPI;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteWordCall extends IdlingResource {
    private final Context context;
    private final LanguageSchoolAPI languageSchoolAPI;
    private final WordDao wordDao;

    public FavoriteWordCall(Context context) {
        this.context = context;
        this.languageSchoolAPI = LanguageSchoolAPIHelper.getApiObject();
        this.wordDao = AppDatabase.getInstance(context).getWordDao();
    }

    public void execute(Word word, HttpResponseInterface<Word> httpResponseInterface) {
        incrementIdlingResource();

        Boolean isFavorite = word.getFavorite();
        FavoriteWordPayload payload =  new FavoriteWordPayload(!isFavorite);

        Call<Word> call = languageSchoolAPI.favoriteWord(getAuthToken(context), payload, word.getId());

        call.enqueue(new Callback<Word>() {
            @Override
            public void onResponse(@NonNull Call<Word> call, @NonNull Response<Word> response) {
                Word returnedWord = response.body();

                if ((response.isSuccessful()) && (returnedWord != null)) {
                    List<Word> wordList = new ArrayList<>();
                    wordList.add(returnedWord);
                    wordDao.save(wordList, result -> {
                        httpResponseInterface.onSuccess(returnedWord);
                        decrementIdlingResource();
                    });
                }else {
                    httpResponseInterface.onError(response);
                    decrementIdlingResource();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Word> call, @NonNull Throwable t) {
                httpResponseInterface.onFailure();
                decrementIdlingResource();
            }
        });
    }
}

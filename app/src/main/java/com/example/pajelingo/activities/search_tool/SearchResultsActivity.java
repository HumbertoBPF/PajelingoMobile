package com.example.pajelingo.activities.search_tool;

import static com.example.pajelingo.utils.Tools.displayFavoriteWordError;
import static com.example.pajelingo.utils.Tools.getAuthToken;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pajelingo.R;
import com.example.pajelingo.adapters.SearchResultsAdapter;
import com.example.pajelingo.daos.WordDao;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.FavoriteWordPayload;
import com.example.pajelingo.models.Word;
import com.example.pajelingo.retrofit.LanguageSchoolAPI;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchResultsActivity extends AppCompatActivity {

    private ConstraintLayout warningConstraintLayout;
    private ImageView warningImageView;
    private TextView warningTextView;
    private RecyclerView resultsRecyclerView;
    private WordDao wordDao;
    private LanguageSchoolAPI languageSchoolAPI;
    private String selectedLanguage;
    private String pattern;
    private SearchResultsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        setTitle(R.string.dictionary_activity_title);

        warningConstraintLayout = findViewById(R.id.warning_constraint_layout);
        warningImageView = findViewById(R.id.warning_image_view);
        warningTextView = findViewById(R.id.warning_text_view);
        resultsRecyclerView = findViewById(R.id.search_recycler_view);

        wordDao = AppDatabase.getInstance(this).getWordDao();

        languageSchoolAPI = LanguageSchoolAPIHelper.getApiObject();

        Intent intent = getIntent();

        pattern = intent.getStringExtra("pattern");
        selectedLanguage = intent.getStringExtra("selectedLanguage");
    }

    @Override
    protected void onResume() {
        super.onResume();
        searchWords();
    }

    private void searchWords() {
        showWarning(R.drawable.loading, R.string.loading_message);

        if (selectedLanguage.equals(getString(R.string.all_languages_spinner_option))){
            wordDao.searchWordsTask("%" + pattern + "%", this::populateRecyclerView).execute();
        }else{
            wordDao.searchWordsTask("%" + pattern + "%", selectedLanguage, this::populateRecyclerView).execute();
        }
    }

    private void showWarning(int warningImage, int warningText) {
        warningConstraintLayout.setVisibility(View.VISIBLE);
        warningImageView.setImageResource(warningImage);
        warningTextView.setText(warningText);
        resultsRecyclerView.setVisibility(View.GONE);
    }

    private void populateRecyclerView(List<Word> words){
        new Handler().postDelayed(() -> {
            if (words.size() == 0){
                showWarning(R.drawable.no_result, R.string.no_results_message);
            }else{
                warningConstraintLayout.setVisibility(View.GONE);
                resultsRecyclerView.setVisibility(View.VISIBLE);
            }

            adapter = new SearchResultsAdapter(SearchResultsActivity.this, words, (word, position) -> {
                Boolean isFavorite = word.getFavorite();
                FavoriteWordPayload payload =  new FavoriteWordPayload(!isFavorite);
                Call<Word> call = languageSchoolAPI.favoriteWord(getAuthToken(SearchResultsActivity.this), payload, word.getId());

                call.enqueue(new Callback<Word>() {
                    @Override
                    public void onResponse(Call<Word> call, Response<Word> response) {
                        Word returnedWord = response.body();

                        if ((response.isSuccessful()) && (returnedWord != null)) {
                            List<Word> wordList = new ArrayList<>();
                            wordList.add(returnedWord);
                            wordDao.getSaveAsyncTask(wordList, result -> {
                                adapter.setWordAtPosition(returnedWord, position);
                            }).execute();
                        }else {
                            displayFavoriteWordError(SearchResultsActivity.this, word);
                        }
                    }

                    @Override
                    public void onFailure(Call<Word> call, Throwable t) {
                        Toast.makeText(SearchResultsActivity.this, R.string.warning_connection_error, Toast.LENGTH_SHORT).show();
                    }
                });
            });

            resultsRecyclerView.setAdapter(adapter);
        }, 2000);
    }
}
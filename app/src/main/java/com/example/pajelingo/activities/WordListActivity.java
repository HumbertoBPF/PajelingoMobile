package com.example.pajelingo.activities;

import static com.example.pajelingo.utils.Tools.displayFavoriteWordError;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pajelingo.R;
import com.example.pajelingo.activities.dictionary.DictionaryFiltersActivity;
import com.example.pajelingo.adapters.DictionaryAdapter;
import com.example.pajelingo.daos.WordDao;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.interfaces.HttpResponseInterface;
import com.example.pajelingo.interfaces.OnResultListener;
import com.example.pajelingo.models.Word;
import com.example.pajelingo.retrofit.LanguageSchoolAPI;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;
import com.example.pajelingo.retrofit_calls.FavoriteWordCall;

import java.util.List;

import retrofit2.Response;

public abstract class WordListActivity extends AppCompatActivity implements OnResultListener<List<Word>> {

    protected ConstraintLayout warningConstraintLayout;
    protected ImageView warningImageView;
    protected TextView warningTextView;
    protected RecyclerView resultsRecyclerView;
    protected WordDao wordDao;
    protected LanguageSchoolAPI languageSchoolAPI;
    protected String selectedLanguage;
    protected String pattern;
    protected DictionaryAdapter adapter;

    protected ActivityResultLauncher<Intent> startActivityIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list);

        warningConstraintLayout = findViewById(R.id.warning_constraint_layout);
        warningImageView = findViewById(R.id.warning_image_view);
        warningTextView = findViewById(R.id.warning_text_view);
        resultsRecyclerView = findViewById(R.id.search_recycler_view);

        wordDao = AppDatabase.getInstance(this).getWordDao();

        languageSchoolAPI = LanguageSchoolAPIHelper.getApiObject();

        pattern = "";
        selectedLanguage = getString(R.string.all_languages_spinner_option);

        startActivityIntent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Intent intent = result.getData();

            if (intent == null) {
                return;
            }

            pattern = intent.getStringExtra("pattern");
            selectedLanguage = intent.getStringExtra("selectedLanguage");
        });
    }

    abstract protected void getWords();

    @Override
    protected void onResume() {
        super.onResume();
        searchWords();
    }

    private void searchWords() {
        showWarning(R.drawable.loading, R.string.loading_message);
        getWords();
    }

    @Override
    public void onResult(List<Word> words) {
        if (words.size() == 0){
            showWarning(R.drawable.no_result, R.string.no_results_message);
        }else{
            warningConstraintLayout.setVisibility(View.GONE);
            resultsRecyclerView.setVisibility(View.VISIBLE);
        }

        adapter = new DictionaryAdapter(WordListActivity.this, words, (word, position) -> {
            FavoriteWordCall favoriteWordCall = new FavoriteWordCall(WordListActivity.this);

            favoriteWordCall.execute(word, new HttpResponseInterface<Word>() {
                @Override
                public void onSuccess(Word word) {
                    adapter.setWordAtPosition(word, position);
                }

                @Override
                public void onError(Response<Word> response) {
                    displayFavoriteWordError(WordListActivity.this, word);
                }

                @Override
                public void onFailure() {
                    Toast.makeText(WordListActivity.this, R.string.warning_connection_error, Toast.LENGTH_SHORT).show();
                }
            });
        });

        resultsRecyclerView.setAdapter(adapter);
    }

    private void showWarning(int warningImage, int warningText) {
        warningConstraintLayout.setVisibility(View.VISIBLE);
        warningImageView.setImageResource(warningImage);
        warningTextView.setText(warningText);
        resultsRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int selectedItemId = item.getItemId();

        if (selectedItemId == R.id.action_filter) {
            Intent intent = new Intent(this, DictionaryFiltersActivity.class);
            intent.putExtra("pattern", pattern);
            intent.putExtra("selectedLanguage", selectedLanguage);
            startActivityIntent.launch(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
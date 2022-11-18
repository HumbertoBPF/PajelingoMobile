package com.example.pajelingo.activities.search_tool;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pajelingo.R;
import com.example.pajelingo.adapters.SearchResultsAdapter;
import com.example.pajelingo.daos.WordDao;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Word;

import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {

    private ConstraintLayout warningConstraintLayout;
    private ImageView warningImageView;
    private TextView warningTextView;
    private RecyclerView resultsRecyclerView;
    private WordDao wordDao;

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

        Intent intent = getIntent();

        String pattern = intent.getStringExtra("pattern");
        String selectedLanguage = intent.getStringExtra("selectedLanguage");

        if (selectedLanguage.equals(getString(R.string.all_languages_spinner_option))){
            wordDao.searchWordsTask("%" + pattern + "%", this::populateRecyclerView).execute();
        }else{
            wordDao.searchWordsTask("%" + pattern + "%", selectedLanguage, this::populateRecyclerView).execute();
        }
    }

    private void populateRecyclerView(List<Word> words){
        new Handler().postDelayed(() -> {
            if (words.size() == 0){
                warningConstraintLayout.setVisibility(View.VISIBLE);
                warningImageView.setImageResource(R.drawable.no_result);
                warningTextView.setText(R.string.no_results_message);
            }else{
                warningConstraintLayout.setVisibility(View.GONE);
            }
            resultsRecyclerView.setAdapter(new SearchResultsAdapter(SearchResultsActivity.this, words));
        }, 2000);
    }
}
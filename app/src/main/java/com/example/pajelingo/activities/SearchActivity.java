package com.example.pajelingo.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pajelingo.R;
import com.example.pajelingo.adapters.SearchResultsAdapter;
import com.example.pajelingo.daos.WordDao;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.interfaces.OnResultListener;
import com.example.pajelingo.models.Word;

import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText searchEditText;
    private Button searchButton;
    private RecyclerView resultsRecyclerView;
    private WordDao wordDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        setTitle(R.string.dictionary_title);

        searchEditText = findViewById(R.id.search_edit_text);
        searchButton = findViewById(R.id.search_button);
        resultsRecyclerView = findViewById(R.id.search_recycler_view);

        wordDao = AppDatabase.getInstance(this).getWordDao();

        searchButton.setOnClickListener(v -> {
            String pattern = searchEditText.getText().toString();
            wordDao.searchWordsTask("%" + pattern + "%", new OnResultListener<List<Word>>() {
                @Override
                public void onResult(List<Word> result) {
                    resultsRecyclerView.setAdapter(new SearchResultsAdapter(SearchActivity.this, result));
                }
            }).execute();
        });
    }
}
package com.example.pajelingo.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pajelingo.R;
import com.example.pajelingo.adapters.SearchResultsRecyclerView;
import com.example.pajelingo.daos.WordDao;
import com.example.pajelingo.database.settings.AppDatabase;

public class SearchActivity extends AppCompatActivity {

    private EditText searchEditText;
    private Button searchButton;
    private RecyclerView recyclerView;
    private WordDao wordDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchEditText = findViewById(R.id.search_edit_text);
        searchButton = findViewById(R.id.search_button);
        recyclerView = findViewById(R.id.results_search_recycler_view);

        wordDao = AppDatabase.getInstance(this).getWordDao();

        searchButton.setOnClickListener(v -> {
            String pattern = searchEditText.getText().toString();
            wordDao.searchWordsTask("%"+pattern+"%", result ->
                    recyclerView.setAdapter(new SearchResultsRecyclerView(SearchActivity.this, result))).execute();
        });
    }
}
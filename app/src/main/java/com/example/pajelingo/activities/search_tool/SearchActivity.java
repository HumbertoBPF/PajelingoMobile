package com.example.pajelingo.activities.search_tool;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pajelingo.R;
import com.example.pajelingo.daos.LanguageDao;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Language;

public class SearchActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private EditText searchEditText;
    private Spinner languageSpinner;
    private Button searchButton;

    private LanguageDao languageDao;

    private String selectedLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        setTitle(R.string.dictionary_title);

        searchEditText = findViewById(R.id.search_edit_text);
        languageSpinner = findViewById(R.id.language_spinner);
        searchButton = findViewById(R.id.search_button);

        languageDao = AppDatabase.getInstance(this).getLanguageDao();

        selectedLanguage = getString(R.string.all_languages_spinner_option);

        languageDao.getAllRecordsTask(result -> {
            result.add(new Language(getString(R.string.all_languages_spinner_option)));
            ArrayAdapter<Language> adapter = new ArrayAdapter<>(SearchActivity.this,
                    android.R.layout.simple_spinner_item, result);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            languageSpinner.setAdapter(adapter);
            languageSpinner.setOnItemSelectedListener(SearchActivity.this);
        }).execute();

        searchButton.setOnClickListener(v -> {
            Intent intent = new Intent(SearchActivity.this, SearchResultsActivity.class);
            intent.putExtra("pattern", searchEditText.getText().toString());
            intent.putExtra("selectedLanguage", selectedLanguage);
            startActivity(intent);
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedLanguage = languageSpinner.getSelectedItem().toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
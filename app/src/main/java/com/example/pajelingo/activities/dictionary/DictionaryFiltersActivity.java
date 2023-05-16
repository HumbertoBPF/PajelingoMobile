package com.example.pajelingo.activities.dictionary;

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
import com.example.pajelingo.ui.LabeledSpinner;

import java.util.List;
import java.util.Objects;

public class DictionaryFiltersActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private EditText searchEditText;
    private Spinner languageSpinner;
    private Button searchButton;

    private LanguageDao languageDao;

    private String selectedLanguage;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        setTitle(R.string.dictionary_activity_title);

        LabeledSpinner languageInput = findViewById(R.id.language_input);

        searchEditText = findViewById(R.id.search_edit_text);
        languageSpinner = languageInput.getSpinner();
        searchButton = findViewById(R.id.search_button);

        languageDao = AppDatabase.getInstance(this).getLanguageDao();

        intent = getIntent();

        if (intent != null) {
            String pattern = intent.getStringExtra("pattern");
            searchEditText.setText(pattern);
        }

        languageDao.getAllRecords(result -> {
            result.add(new Language(getString(R.string.all_languages_spinner_option)));
            ArrayAdapter<Language> adapter = new ArrayAdapter<>(DictionaryFiltersActivity.this,
                    android.R.layout.simple_spinner_item, result);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            languageSpinner.setAdapter(adapter);
            languageSpinner.setOnItemSelectedListener(DictionaryFiltersActivity.this);
            int position = getLanguageItemPosition(result);
            languageSpinner.setSelection(position);
        });

        searchButton.setOnClickListener(v -> {
            Intent resultIntent = new Intent(DictionaryFiltersActivity.this, DictionaryActivity.class);
            resultIntent.putExtra("pattern", searchEditText.getText().toString());
            resultIntent.putExtra("selectedLanguage", selectedLanguage);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    private int getLanguageItemPosition(List<Language> languages) {
        if (intent != null) {
            String selectedLanguage = intent.getStringExtra("selectedLanguage");

            for (int i = 0;i < languages.size();i++) {
                Language language = languages.get(i);

                if (Objects.equals(selectedLanguage, language.getLanguageName())){
                    return i;
                }
            }
        }

        return 0;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedLanguage = languageSpinner.getSelectedItem().toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
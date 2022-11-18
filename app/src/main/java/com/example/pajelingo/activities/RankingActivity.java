package com.example.pajelingo.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pajelingo.R;
import com.example.pajelingo.adapters.ScoreAdapter;
import com.example.pajelingo.daos.LanguageDao;
import com.example.pajelingo.daos.ScoreDao;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Language;

public class RankingActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner languageSpinner;
    private RecyclerView rankingRecyclerView;
    private LanguageDao languageDao;
    private ScoreDao scoreDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        setTitle(R.string.ranking_activity_title);

        languageSpinner = findViewById(R.id.language_spinner);
        rankingRecyclerView = findViewById(R.id.ranking_recycler_view);

        languageDao = AppDatabase.getInstance(this).getLanguageDao();
        scoreDao = AppDatabase.getInstance(this).getScoreDao();

        languageDao.getAllRecordsTask(result -> {
            result.add(new Language(getString(R.string.general_ranking_spinner_text)));
            ArrayAdapter<Language> adapter = new ArrayAdapter<>(RankingActivity.this,
                    android.R.layout.simple_spinner_item, result);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            languageSpinner.setAdapter(adapter);
            languageSpinner.setOnItemSelectedListener(RankingActivity.this);
        }).execute();

        showGeneralRanking();
    }

    private void showGeneralRanking() {
        scoreDao.getAllScoresSortedTask(result -> {
            int lastPosition = languageSpinner.getAdapter().getCount();
            languageSpinner.setSelection(lastPosition - 1);
            rankingRecyclerView.setAdapter(new ScoreAdapter(result));
        }).execute();
    }

    private void applyFilter() {
        String selectedLanguage = languageSpinner.getSelectedItem().toString();
        if (selectedLanguage.equals(getString(R.string.general_ranking_spinner_text))){
            showGeneralRanking();
        }else{
            scoreDao.getAllScoresSortedTask(selectedLanguage,
                    result -> rankingRecyclerView.setAdapter(new ScoreAdapter(result))).execute();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        applyFilter();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
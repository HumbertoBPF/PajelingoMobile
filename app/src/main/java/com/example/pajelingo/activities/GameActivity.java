package com.example.pajelingo.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pajelingo.R;
import com.example.pajelingo.daos.LanguageDao;
import com.example.pajelingo.database.settings.AppDatabase;

public abstract class GameActivity extends AppCompatActivity {

    protected LanguageDao languageDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        languageDao = AppDatabase.getInstance(this).getLanguageDao();
        setup();
    }

    protected abstract void setup();

    protected abstract void startGame();

    protected abstract void verifyAnswer(Object answer);

    protected void finishActivityNotEnoughResources(){
        Toast.makeText(this, getString(R.string.warning_no_resources), Toast.LENGTH_LONG).show();
        finish();
    }

}
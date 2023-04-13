package com.example.pajelingo.activities.account;

import android.os.Bundle;

import com.example.pajelingo.R;
import com.example.pajelingo.activities.WordListActivity;

public class FavoriteWordsActivity extends WordListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.favorite_words_activity_title);
    }

    @Override
    protected void getWords() {
        if (selectedLanguage.equals(getString(R.string.all_languages_spinner_option))){
            wordDao.searchFavoriteWordsTask("%" + pattern + "%", this).execute();
        }else{
            wordDao.searchFavoriteWordsTask("%" + pattern + "%", selectedLanguage, this).execute();
        }
    }
}
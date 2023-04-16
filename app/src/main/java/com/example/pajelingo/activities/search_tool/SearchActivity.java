package com.example.pajelingo.activities.search_tool;

import android.os.Bundle;

import com.example.pajelingo.R;
import com.example.pajelingo.activities.WordListActivity;

public class SearchActivity extends WordListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.dictionary_activity_title);
    }

    @Override
    protected void getWords() {
        if (selectedLanguage.equals(getString(R.string.all_languages_spinner_option))){
            wordDao.searchWords("%" + pattern + "%", this);
        }else{
            wordDao.searchWords("%" + pattern + "%", selectedLanguage, this);
        }
    }
}
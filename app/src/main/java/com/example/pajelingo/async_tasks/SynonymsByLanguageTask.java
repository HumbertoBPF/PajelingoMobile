package com.example.pajelingo.async_tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.example.pajelingo.daos.WordDao;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.interfaces.OnResultListener;
import com.example.pajelingo.models.Language;
import com.example.pajelingo.models.Word;

import java.util.ArrayList;
import java.util.List;

public class SynonymsByLanguageTask extends AsyncTask<Void, Void, List<String>> {
    private final Context context;
    private final OnResultListener<List<String>> onResultListener;
    private final Word word;
    private final Language language;

    public SynonymsByLanguageTask(Context context, Word word, Language language, OnResultListener<List<String>> onResultListener){
        this.context = context;
        this.word = word;
        this.language = language;
        this.onResultListener = onResultListener;
    }

    @Override
    protected List<String> doInBackground(Void... voids) {
        WordDao wordDao = AppDatabase.getInstance(context).getWordDao();

        List<String> synonyms = new ArrayList<>();

        for (Long synonymId : word.getIdsSynonyms()){
            Word synonym = wordDao.findRecordById(synonymId);
            // Return only the synonyms whose language matches with the specified one
            if (synonym.getIdLanguage().equals(language.getId())){
                synonyms.add(synonym.getWordName());
            }
        }

        return synonyms;
    }

    @Override
    protected void onPostExecute(List<String> synonyms) {
        super.onPostExecute(synonyms);
        this.onResultListener.onResult(synonyms);
    }
}

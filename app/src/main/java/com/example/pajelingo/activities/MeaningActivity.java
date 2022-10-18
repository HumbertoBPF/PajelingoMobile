package com.example.pajelingo.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pajelingo.R;
import com.example.pajelingo.adapters.MeaningAdapter;
import com.example.pajelingo.daos.MeaningDao;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.interfaces.OnResultListener;
import com.example.pajelingo.models.Meaning;
import com.example.pajelingo.models.Word;

import java.util.List;

public class MeaningActivity extends AppCompatActivity {

    private RecyclerView meaningsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meaning);

        meaningsRecyclerView = findViewById(R.id.meanings_recycler_view);

        Word word = (Word) getIntent().getSerializableExtra("word");

        setTitle(word.getWordName());

        MeaningDao meaningDao = AppDatabase.getInstance(MeaningActivity.this).getMeaningDao();
        meaningDao.getMeaningOfWordTask(word.getId(), new OnResultListener<List<Meaning>>() {
            @Override
            public void onResult(List<Meaning> result) {
                meaningsRecyclerView.setAdapter(new MeaningAdapter(result));
            }
        }).execute();
    }
}
package com.example.pajelingo.activities;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pajelingo.R;
import com.example.pajelingo.adapters.ScoreAdapter;
import com.example.pajelingo.daos.LanguageDao;
import com.example.pajelingo.daos.ScoreDao;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.interfaces.OnResultListener;
import com.example.pajelingo.models.Language;
import com.example.pajelingo.models.Score;
import com.example.pajelingo.synchronization.ScoreSynchro;

import java.util.List;

public class RankingActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner languageSpinner;
    private RecyclerView rankingRecyclerView;
    private LanguageDao languageDao;
    private ScoreDao scoreDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        languageSpinner = findViewById(R.id.language_spinner);
        rankingRecyclerView = findViewById(R.id.ranking_recycler_view);

        languageDao = AppDatabase.getInstance(this).getLanguageDao();
        scoreDao = AppDatabase.getInstance(this).getScoreDao();

        languageDao.getAllRecordsTask(new OnResultListener<List<Language>>() {
            @Override
            public void onResult(List<Language> result) {
                result.add(new Language(getString(R.string.general_ranking_spinner_text)));
                ArrayAdapter<Language> adapter = new ArrayAdapter<>(RankingActivity.this,
                        android.R.layout.simple_spinner_item, result);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                languageSpinner.setAdapter(adapter);
                languageSpinner.setOnItemSelectedListener(RankingActivity.this);
            }
        }).execute();

        showGeneralRanking();
    }

    private void showGeneralRanking() {
        scoreDao.getAllScoresSortedTask(new OnResultListener<List<Score>>() {
            @Override
            public void onResult(List<Score> result) {
                int lastPosition = languageSpinner.getAdapter().getCount();
                languageSpinner.setSelection(lastPosition - 1);
                rankingRecyclerView.setAdapter(new ScoreAdapter(result));
            }
        }).execute();
    }

    private void applyFilter() {
        String selectedLanguage = languageSpinner.getSelectedItem().toString();
        if (selectedLanguage.equals(getString(R.string.general_ranking_spinner_text))){
            showGeneralRanking();
        }else{
            scoreDao.getAllScoresSortedTask(selectedLanguage, new OnResultListener<List<Score>>() {
                @Override
                public void onResult(List<Score> result) {
                    rankingRecyclerView.setAdapter(new ScoreAdapter(result));
                }
            }).execute();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_appbar, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_online).setVisible(false);
        menu.findItem(R.id.action_rankings).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_synchro){
            launchRankingUpdate();
        }
        return super.onOptionsItemSelected(item);
    }

    private void launchRankingUpdate() {
        SharedPreferences sp = getSharedPreferences(getString(R.string.sp_file_name), MODE_PRIVATE);

        String username = sp.getString(getString(R.string.username_sp),"");
        String password = sp.getString(getString(R.string.password_sp),"");

        String lastSynchroDate = sp.getString(getString(R.string.last_score_synchro_sp), getString(R.string.default_last_synchro_date));
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle(R.string.confirm_update_rankings_dialog_title)
                .setMessage("Do you want to update the rankings ? (Last update: "+lastSynchroDate+")")
                .setPositiveButton(R.string.yes_button_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        new ScoreSynchro(RankingActivity.this, username, password, result -> {
                            showGeneralRanking();
                            Toast.makeText(RankingActivity.this,
                                    R.string.update_ranking_success_message, Toast.LENGTH_SHORT).show();
                        }).execute();
                    }
                })
                .setNegativeButton(R.string.no_button_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        applyFilter();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
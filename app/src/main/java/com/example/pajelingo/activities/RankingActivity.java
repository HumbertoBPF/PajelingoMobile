package com.example.pajelingo.activities;

import static com.example.pajelingo.util.Tools.getAuthToken;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RankingActivity extends AppCompatActivity {

    private Spinner languageSpinner;
    private Button searchButton;
    private RecyclerView rankingRecyclerView;
    private LanguageDao languageDao;
    private ScoreDao scoreDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        languageSpinner = findViewById(R.id.language_spinner);
        searchButton = findViewById(R.id.search_button);
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

                searchButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        applyFilter();
                    }
                });
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
            AlertDialog updateDialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.update_ranking_dialog_title).setMessage(R.string.update_ranking_dialog_message)
                    .setCancelable(false).create();
            updateDialog.show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    launchRankingUpdate(updateDialog);
                }
            }, 2000);
            launchRankingUpdate(updateDialog);
        }
        return super.onOptionsItemSelected(item);
    }
    private void launchRankingUpdate(AlertDialog updateDialog) {
        SharedPreferences sp = getSharedPreferences(getString(R.string.sp_file_name), MODE_PRIVATE);

        String username = sp.getString(getString(R.string.username_sp),"");
        String password = sp.getString(getString(R.string.password_sp),"");

        Call<List<Score>> call = LanguageSchoolAPIHelper
                .getApiObject().getScores(getAuthToken(username, password));
        call.enqueue(new Callback<List<Score>>() {
            @Override
            public void onResponse(Call<List<Score>> call, Response<List<Score>> response) {
                if (response.isSuccessful()){
                    List<Score> scores = response.body();
                    scoreDao.getSaveAsyncTask(scores, new OnResultListener<List<Score>>() {
                        @Override
                        public void onResult(List<Score> result) {
                            showGeneralRanking();
                            Toast.makeText(RankingActivity.this, R.string.update_ranking_success_message, Toast.LENGTH_SHORT).show();
                            updateDialog.dismiss();
                        }
                    }).execute();
                }else{
                    Toast.makeText(RankingActivity.this,
                            R.string.update_ranking_fail_message, Toast.LENGTH_SHORT).show();
                    updateDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<List<Score>> call, Throwable t) {
                Toast.makeText(RankingActivity.this,
                        R.string.update_ranking_fail_message, Toast.LENGTH_SHORT).show();
                updateDialog.dismiss();
            }
        });
    }
}
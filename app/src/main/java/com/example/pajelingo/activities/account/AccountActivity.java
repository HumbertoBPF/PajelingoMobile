package com.example.pajelingo.activities.account;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pajelingo.R;
import com.example.pajelingo.adapters.ScoreAdapter;
import com.example.pajelingo.daos.LanguageDao;
import com.example.pajelingo.daos.ScoreDao;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Language;
import com.example.pajelingo.models.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AccountActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    protected TextView usernameCredentialTextView;
    protected TextView emailCredentialTextView;
    protected TextView bioTextView;
    protected ImageView profilePictureImageView;
    protected MaterialButton editAccountButton;
    protected MaterialButton deleteAccountButton;
    protected Spinner languageSpinner;
    protected RecyclerView scoreRecyclerView;
    protected FloatingActionButton favoriteButton;

    private LanguageDao languageDao;
    private ScoreDao scoreDao;

    protected User user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        usernameCredentialTextView = findViewById(R.id.username_text_view);
        emailCredentialTextView = findViewById(R.id.email_text_view);
        bioTextView = findViewById(R.id.bio_text_view);
        profilePictureImageView = findViewById(R.id.profile_picture_image_view);
        editAccountButton = findViewById(R.id.edit_account_button);
        deleteAccountButton = findViewById(R.id.delete_account_button);
        languageSpinner = findViewById(R.id.language_spinner);
        scoreRecyclerView = findViewById(R.id.score_recycler_view);
        favoriteButton = findViewById(R.id.favorite_button);

        languageDao = AppDatabase.getInstance(this).getLanguageDao();
        scoreDao = AppDatabase.getInstance(this).getScoreDao();
    }

    @Override
    protected void onResume() {
        super.onResume();

        languageDao.getAllRecords(result -> {
            ArrayAdapter<Language> adapter = new ArrayAdapter<>(AccountActivity.this,
                    android.R.layout.simple_spinner_item, result);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            languageSpinner.setAdapter(adapter);
            languageSpinner.setOnItemSelectedListener(AccountActivity.this);
            applyLanguageFilter();
        });
    }

    private void applyLanguageFilter(){
        Object selectedItem = languageSpinner.getSelectedItem();

        if ((user != null) && (selectedItem != null)){
            String selectedLanguage = languageSpinner.getSelectedItem().toString();
            scoreDao.getScoresByUserAndByLanguage(user.getUsername(), selectedLanguage,
                    result -> scoreRecyclerView.setAdapter(new ScoreAdapter(result)));
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        applyLanguageFilter();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
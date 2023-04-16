package com.example.pajelingo.activities.account;

import static com.example.pajelingo.utils.Tools.getPictureFromBase64String;
import static com.example.pajelingo.utils.Tools.isUserAuthenticated;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
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

public class ProfileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private SharedPreferences sp;

    private TextView usernameCredentialTextView;
    private TextView emailCredentialTextView;
    private ImageView profilePictureImageView;
    private MaterialButton editAccountButton;
    private MaterialButton deleteAccountButton;
    private Spinner languageSpinner;
    private RecyclerView scoreRecyclerView;
    private FloatingActionButton favoriteButton;

    private LanguageDao languageDao;
    private ScoreDao scoreDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setTitle(getString(R.string.profile_activity_title));

        sp =  getSharedPreferences(getString(R.string.sp_file_name), MODE_PRIVATE);

        usernameCredentialTextView = findViewById(R.id.username_credential_text_view);
        emailCredentialTextView = findViewById(R.id.email_credential_text_view);
        profilePictureImageView = findViewById(R.id.profile_picture_image_view);
        editAccountButton = findViewById(R.id.edit_account_button);
        deleteAccountButton = findViewById(R.id.delete_account_button);
        languageSpinner = findViewById(R.id.language_spinner);
        scoreRecyclerView = findViewById(R.id.score_recycler_view);
        favoriteButton = findViewById(R.id.favorite_button);

        languageDao = AppDatabase.getInstance(this).getLanguageDao();
        scoreDao = AppDatabase.getInstance(this).getScoreDao();

        languageDao.getAllRecords(result -> {
            ArrayAdapter<Language> adapter = new ArrayAdapter<>(ProfileActivity.this,
                    android.R.layout.simple_spinner_item, result);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            languageSpinner.setAdapter(adapter);
            languageSpinner.setOnItemSelectedListener(ProfileActivity.this);
            applyLanguageFilter();
        });

        deleteAccountButton.setOnClickListener(v -> askConfirmationDeletion());

        setProfilePicture();

        favoriteButton.setOnClickListener(v -> {
            startActivity(new Intent(this, FavoriteWordsActivity.class));
        });
    }

    private void setProfilePicture() {
        String picture = sp.getString(getString(R.string.picture_sp), null);
        if (picture != null){
            profilePictureImageView.setImageBitmap(getPictureFromBase64String(picture));
        }
    }

    private void askConfirmationDeletion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_delete_account_title)
                .setMessage(R.string.dialog_delete_account_message)
                .setPositiveButton(R.string.dialog_delete_account_confirm, (dialog, id) -> {
                    startActivity(new Intent(ProfileActivity.this, DeletionConfirmationActivity.class));
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.dialog_delete_account_decline, (dialog, which) -> dialog.dismiss());
        AlertDialog confirmationDialog = builder.create();
        confirmationDialog.setCancelable(false);
        confirmationDialog.show();
    }

    private void applyLanguageFilter(){
        Object selectedItem = languageSpinner.getSelectedItem();

        if (selectedItem != null){
            String username = sp.getString(getString(R.string.username_sp), null);
            String selectedLanguage = languageSpinner.getSelectedItem().toString();
            scoreDao.getScoresByUserAndByLanguage(username, selectedLanguage,
                    result -> scoreRecyclerView.setAdapter(new ScoreAdapter(result)));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUserCredentials();
        // Verifies if the user credentials were deleted while he was out from this activity
        if (!isUserAuthenticated(this)){
            finish();
        }
    }

    private void updateUserCredentials() {
        String username = sp.getString(getString(R.string.username_sp),  "");
        String email = sp.getString(getString(R.string.email_sp), "");

        usernameCredentialTextView.setText(getString(R.string.username_label)+": "+username);
        emailCredentialTextView.setText(getString(R.string.email_label)+": "+email);

        editAccountButton.setOnClickListener(v -> {
            User authenticatedUser = new User(email, username, null, null);
            Intent intent = new Intent(ProfileActivity.this, FormUserActivity.class);
            intent.putExtra("authenticatedUser", authenticatedUser);
            startActivity(intent);
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        applyLanguageFilter();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
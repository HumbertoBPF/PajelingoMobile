package com.example.pajelingo.activities.account;

import static com.example.pajelingo.utils.Files.getPictureFromBase64String;
import static com.example.pajelingo.utils.SharedPreferences.isUserAuthenticated;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;

import com.example.pajelingo.R;
import com.example.pajelingo.models.User;

public class MyProfileActivity extends AccountActivity {
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(getString(R.string.profile_activity_title));

        sp =  getSharedPreferences(getString(R.string.sp_file_name), MODE_PRIVATE);

        deleteAccountButton.setOnClickListener(v -> askConfirmationDeletion());

        favoriteButton.setOnClickListener(v -> startActivity(new Intent(this, FavoriteWordsActivity.class)));
    }

    private void askConfirmationDeletion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_delete_account_title)
                .setMessage(R.string.dialog_delete_account_message)
                .setPositiveButton(R.string.dialog_delete_account_confirm, (dialog, id) -> {
                    startActivity(new Intent(MyProfileActivity.this, DeletionConfirmationActivity.class));
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.dialog_delete_account_decline, (dialog, which) -> dialog.dismiss());
        AlertDialog confirmationDialog = builder.create();
        confirmationDialog.setCancelable(false);
        confirmationDialog.show();
    }

    @Override
    protected void onResume() {
        updateUserCredentials();
        // Verifies if the user credentials were deleted while he was out from this activity
        if (!isUserAuthenticated(this)){
            finish();
        }
        super.onResume();
    }

    private void updateUserCredentials() {
        String username = sp.getString(getString(R.string.username_sp),  "");
        String email = sp.getString(getString(R.string.email_sp), "");
        String picture = sp.getString(getString(R.string.picture_sp), null);

        user = new User(email, username, null, picture);

        usernameCredentialTextView.setText(getString(R.string.account_username, username));
        emailCredentialTextView.setText(getString(R.string.account_email, email));

        if (picture != null){
            profilePictureImageView.setImageBitmap(getPictureFromBase64String(picture));
        }

        editAccountButton.setOnClickListener(v -> {
            Intent intent = new Intent(MyProfileActivity.this, FormUserActivity.class);
            intent.putExtra("authenticatedUser", user);
            startActivity(intent);
        });
    }
}
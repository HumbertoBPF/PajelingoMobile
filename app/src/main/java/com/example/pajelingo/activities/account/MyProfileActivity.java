package com.example.pajelingo.activities.account;

import static com.example.pajelingo.utils.Files.getPictureFromBase64String;
import static com.example.pajelingo.utils.SharedPreferences.getBadgesFromSharedPreferences;
import static com.example.pajelingo.utils.SharedPreferences.isUserAuthenticated;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.pajelingo.R;
import com.example.pajelingo.interfaces.HttpResponseInterface;
import com.example.pajelingo.models.Badge;
import com.example.pajelingo.models.User;
import com.example.pajelingo.retrofit_calls.UserDataCall;

import java.util.List;

import retrofit2.Response;

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
        updateUserData();
        displayUserData();
        // Verifies if the user credentials were deleted while he was out from this activity
        if (!isUserAuthenticated(this)){
            finish();
        }
        super.onResume();
    }

    private void updateUserData() {
        UserDataCall userDataCall = new UserDataCall(this);
        userDataCall.execute(new HttpResponseInterface<User>() {
            @Override
            public void onSuccess(User user) {
                displayUserData();
            }

            @Override
            public void onError(Response<User> response) {
                Toast.makeText(MyProfileActivity.this, R.string.warning_outdated_data, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure() {
                Toast.makeText(MyProfileActivity.this, R.string.warning_outdated_data, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayUserData() {
        String username = sp.getString(getString(R.string.username_sp),  "");
        String email = sp.getString(getString(R.string.email_sp), "");
        String picture = sp.getString(getString(R.string.picture_sp), null);
        String bio = sp.getString(getString(R.string.bio_sp), null);
        List<Badge> badges = getBadgesFromSharedPreferences(this, sp);

        user = new User(email, username, null, picture, bio, badges);

        usernameCredentialTextView.setText(getString(R.string.account_username, username));
        emailCredentialTextView.setText(getString(R.string.account_email, email));
        bioTextView.setText(getString(R.string.account_bio, bio));

        if (picture != null){
            profilePictureImageView.setImageBitmap(getPictureFromBase64String(picture));
        }

        displayBadges();

        editAccountButton.setOnClickListener(v -> {
            Intent intent = new Intent(MyProfileActivity.this, FormUserActivity.class);
            intent.putExtra("authenticatedUser", user);
            startActivity(intent);
        });
    }
}
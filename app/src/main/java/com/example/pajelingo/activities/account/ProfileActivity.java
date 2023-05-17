package com.example.pajelingo.activities.account;

import static com.example.pajelingo.utils.Files.getPictureFromBase64String;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.pajelingo.R;
import com.example.pajelingo.models.User;
import com.example.pajelingo.retrofit.LanguageSchoolAPI;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AccountActivity {
    private LanguageSchoolAPI languageSchoolAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        emailCredentialTextView.setVisibility(View.GONE);
        editAccountButton.setVisibility(View.GONE);
        deleteAccountButton.setVisibility(View.GONE);
        favoriteButton.setVisibility(View.GONE);

        languageSchoolAPI = LanguageSchoolAPIHelper.getApiObject();

        Intent intent = getIntent();

        if (intent == null) {
            finish();
            return;
        }

        user = (User) intent.getSerializableExtra("account");
        setUserCredentials();

        Call<User> call = languageSchoolAPI.getAccount(user.getUsername());

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                User responseUser = response.body();

                if (response.isSuccessful() && (responseUser != null)) {
                    user = responseUser;
                    setUserCredentials();
                } else {
                    Toast.makeText(ProfileActivity.this, R.string.warning_request_error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Toast.makeText(ProfileActivity.this, R.string.warning_connection_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUserCredentials() {
        usernameCredentialTextView.setText(getString(R.string.account_username, user.getUsername()));

        String base64Picture = user.getPicture();

        if (base64Picture != null) {
            profilePictureImageView.setImageBitmap(getPictureFromBase64String(base64Picture));
        }
    }
}
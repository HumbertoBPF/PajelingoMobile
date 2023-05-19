package com.example.pajelingo.activities.account;

import static com.example.pajelingo.utils.Files.getPictureFromBase64String;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.pajelingo.R;
import com.example.pajelingo.interfaces.HttpResponseInterface;
import com.example.pajelingo.models.User;
import com.example.pajelingo.retrofit_calls.SearchAccountCall;

import retrofit2.Response;

public class ProfileActivity extends AccountActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        emailCredentialTextView.setVisibility(View.GONE);
        editAccountButton.setVisibility(View.GONE);
        deleteAccountButton.setVisibility(View.GONE);
        favoriteButton.setVisibility(View.GONE);

        Intent intent = getIntent();

        if (intent == null) {
            finish();
            return;
        }

        user = (User) intent.getSerializableExtra("account");
        setUserCredentials();

        SearchAccountCall call = new SearchAccountCall();

        call.execute(user.getUsername(), new HttpResponseInterface<User>() {
            @Override
            public void onSuccess(User responseUser) {
                user = responseUser;
                setUserCredentials();
            }

            @Override
            public void onError(Response<User> response) {
                Toast.makeText(ProfileActivity.this, R.string.warning_request_error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure() {
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
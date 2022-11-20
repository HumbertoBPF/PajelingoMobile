package com.example.pajelingo.activities.account;

import static com.example.pajelingo.utils.Tools.getAuthToken;
import static com.example.pajelingo.utils.Tools.saveStateAndUserCredentials;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pajelingo.R;
import com.example.pajelingo.models.User;
import com.example.pajelingo.retrofit.LanguageSchoolAPI;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private final LanguageSchoolAPI languageSchoolAPI = LanguageSchoolAPIHelper.getApiObject();

    private TextView signupLinkTextView;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitle(R.string.login_activity_title);

        signupLinkTextView = findViewById(R.id.signup_link_text_view);
        usernameEditText = findViewById(R.id.username_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(v -> {
            login();
        });

        signupLinkTextView.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            finish();
        });
    }

    private void login(){
        AlertDialog dialog = new AlertDialog.Builder(LoginActivity.this).setTitle(R.string.login_dialog_title)
                .setMessage(R.string.login_dialog_message).setCancelable(false).create();
        dialog.show();

        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        Call<User> call = languageSchoolAPI.login(getAuthToken(username, password));
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User user = response.body();
                if ((response.isSuccessful()) && (user != null)){
                    Log.i("USER", user.getUsername());
                    Log.i("USER", user.getEmail());
                    saveStateAndUserCredentials(getApplicationContext(), user.getUsername(), user.getEmail(), password);
                    new Handler().postDelayed(() -> {
                        Toast.makeText(LoginActivity.this, "Welcome, "+username, Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                        finish();
                    }, 2000);
                }else {
                    if (response.code() == 401) {
                        dialog.setMessage(getString(R.string.warning_invalid_credientials));
                    } else {
                        dialog.setMessage(getString(R.string.warning_fail_login));
                    }
                    dismissDialogDelayed(dialog);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                dialog.setMessage(getString(R.string.warning_fail_login));
                dismissDialogDelayed(dialog);
            }
        });
    }

    private void dismissDialogDelayed(AlertDialog dialog){
        new Handler().postDelayed(dialog::dismiss,2000);
    }
}
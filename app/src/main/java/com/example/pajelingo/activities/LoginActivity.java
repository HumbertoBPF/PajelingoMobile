package com.example.pajelingo.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pajelingo.R;
import com.example.pajelingo.interfaces.OnResultListener;
import com.example.pajelingo.models.Score;
import com.example.pajelingo.synchronization.ScoreSynchro;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.username_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                new ScoreSynchro(LoginActivity.this, username, password, new OnResultListener<List<Score>>() {
                    @Override
                    public void onResult(List<Score> result) {
                        Toast.makeText(LoginActivity.this, "Welcome, "+username, Toast.LENGTH_LONG).show();
                        saveStateAndUserCredentials(username, password);
                        finish();
                    }
                }).execute();
            }
        });
    }

    private void saveStateAndUserCredentials(String username, String password) {
        // Save mode and credentials in SharedPreferences
        SharedPreferences sp = getSharedPreferences(getString(R.string.sp_file_name),MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(getString(R.string.username_sp), username);
        editor.putString(getString(R.string.password_sp), password);
        editor.apply();
    }

}
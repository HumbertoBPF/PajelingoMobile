package com.example.pajelingo.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pajelingo.R;
import com.example.pajelingo.daos.ScoreDao;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Score;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;

import java.nio.charset.StandardCharsets;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
                Call<List<Score>> loginCall = LanguageSchoolAPIHelper.getApiObject().getScores(getAuthToken(username, password));
                loginCall.enqueue(new Callback<List<Score>>() {
                    @Override
                    public void onResponse(Call<List<Score>> call, Response<List<Score>> response) {
                        if (response.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "Welcome, "+username, Toast.LENGTH_LONG).show();
                            saveStateAndUserCredentials(username, password);
                            // Save scores
                            ScoreDao scoreDao = AppDatabase.getInstance(LoginActivity.this).getScoreDao();
                            scoreDao.getSaveAsyncTask(response.body()).execute();

                            finish();
                        }else if (response.code() == 401){
                            Toast.makeText(LoginActivity.this, R.string.warning_invalid_credientials, Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(LoginActivity.this, R.string.warning_fail_login, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Score>> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, R.string.warning_fail_login, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void saveStateAndUserCredentials(String username, String password) {
        // Save mode and credentials in SharedPreferences
        SharedPreferences sp = getSharedPreferences(getString(R.string.sp_file_name),MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(getString(R.string.is_online_mode_sp), true);
        editor.putString(getString(R.string.username_sp), username);
        editor.putString(getString(R.string.password_sp), password);
        editor.apply();
    }

    private String getAuthToken(String username, String password) {
        byte[] data = (username + ":" + password).getBytes(StandardCharsets.UTF_8);
        return "Basic " + Base64.encodeToString(data, Base64.NO_WRAP);
    }

}
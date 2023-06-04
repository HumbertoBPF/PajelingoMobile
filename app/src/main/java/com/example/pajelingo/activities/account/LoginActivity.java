package com.example.pajelingo.activities.account;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pajelingo.R;
import com.example.pajelingo.interfaces.HttpResponseInterface;
import com.example.pajelingo.models.User;
import com.example.pajelingo.retrofit_calls.LoginCall;
import com.example.pajelingo.ui.LabeledEditText;
import com.example.pajelingo.ui.LoadingButton;

import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private TextView signupLinkTextView;
    private TextView resetPasswordLinkTextView;
    private LabeledEditText usernameInput;
    private LabeledEditText passwordInput;
    private LoadingButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitle(R.string.login_activity_title);

        signupLinkTextView = findViewById(R.id.signup_link_text_view);
        resetPasswordLinkTextView = findViewById(R.id.reset_password_link_text_view);
        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(v -> login());

        signupLinkTextView.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, FormUserActivity.class));
            finish();
        });

        resetPasswordLinkTextView.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RequestResetPasswordActivity.class));
            finish();
        });
    }

    private void login(){
        String username = usernameInput.getEditText().getText().toString();
        String password = passwordInput.getEditText().getText().toString();

        loginButton.setLoading(true);

        LoginCall call = new LoginCall(this);

        call.execute(username, password, new HttpResponseInterface<User>() {
            @Override
            public void onSuccess(User user) {
                Toast.makeText(LoginActivity.this, "Welcome, "+user.getUsername(), Toast.LENGTH_LONG).show();
                loginButton.setLoading(false);
                finish();
            }

            @Override
            public void onError(Response<User> response) {
                Toast.makeText(LoginActivity.this, R.string.warning_invalid_credentials, Toast.LENGTH_SHORT).show();
                loginButton.setLoading(false);
            }

            @Override
            public void onFailure() {
                Toast.makeText(LoginActivity.this, R.string.warning_connection_error, Toast.LENGTH_SHORT).show();
                loginButton.setLoading(false);
            }
        });
    }
}
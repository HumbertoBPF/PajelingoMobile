package com.example.pajelingo.activities.account;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pajelingo.R;

public class SignupActivity extends AppCompatActivity {

    private TextView loginLinkTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        setTitle(getString(R.string.signup_activity_title));

        loginLinkTextView = findViewById(R.id.login_link_text_view);

        loginLinkTextView.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            finish();
        });
    }
}
package com.example.pajelingo.activities.account;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import com.example.pajelingo.R;

public class ProfileActivity extends AppCompatActivity {
    private TextView usernameCredentialTextView;
    private TextView emailCredentialTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setTitle(getString(R.string.profile_activity_title));

        usernameCredentialTextView = findViewById(R.id.username_credential_text_view);
        emailCredentialTextView = findViewById(R.id.email_credential_text_view);

        SharedPreferences sp = getSharedPreferences(getString(R.string.sp_file_name), MODE_PRIVATE);

        usernameCredentialTextView.setText(getString(R.string.username_label)+":\n"+sp.getString(getString(R.string.username_sp),  ""));
        emailCredentialTextView.setText(getString(R.string.email_label)+":\n"+sp.getString(getString(R.string.email_sp), ""));
    }
}
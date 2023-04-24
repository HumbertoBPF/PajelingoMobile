package com.example.pajelingo.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pajelingo.R;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new Handler().postDelayed(() -> {
            SharedPreferences sp = getSharedPreferences(getString(R.string.sp_file_name),MODE_PRIVATE);

            if (sp.getBoolean(getString(R.string.is_first_access), true)){
                startActivity(new Intent(SplashScreen.this, OnBoardingActivity.class));
            }else {
                startActivity(new Intent(SplashScreen.this, MainActivity.class));
            }

            finish();
        },3000);
    }
}
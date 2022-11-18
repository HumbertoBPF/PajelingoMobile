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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sp = getSharedPreferences(getString(R.string.sp_file_name),MODE_PRIVATE);

                Class destinyActivity = MainActivity.class;
                if (sp.getBoolean(getString(R.string.is_first_access), true)){
                    destinyActivity = OnBoardingActivity.class;
                }

                startActivity(new Intent(SplashScreen.this, destinyActivity));
                finish();
            }
        },3000);
    }
}
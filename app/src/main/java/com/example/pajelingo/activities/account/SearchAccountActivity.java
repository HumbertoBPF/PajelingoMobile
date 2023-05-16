package com.example.pajelingo.activities.account;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.pajelingo.R;

public class SearchAccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_account);

        setTitle(R.string.search_account_menu_item_title);
    }
}
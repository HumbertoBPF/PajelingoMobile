package com.example.pajelingo.activities;

import static com.example.pajelingo.utils.SharedPreferences.isUserAuthenticated;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pajelingo.R;
import com.example.pajelingo.activities.account.ProfileActivity;
import com.example.pajelingo.ui.SimpleListItem;

public class MenuActivity extends AppCompatActivity {
    private SimpleListItem profileItem;
    private SimpleListItem rankingsItem;
    private SimpleListItem faqItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        profileItem = findViewById(R.id.menu_item_profile);
        rankingsItem = findViewById(R.id.menu_item_rankings);
        faqItem = findViewById(R.id.menu_item_faq);

        if (!isUserAuthenticated(MenuActivity.this)){
            profileItem.setVisibility(View.GONE);
        }

        profileItem.setOnClickListener(v -> {
            startActivity(new Intent(MenuActivity.this, ProfileActivity.class));
            finish();
        });

        rankingsItem.setOnClickListener(v -> {
            startActivity(new Intent(MenuActivity.this, RankingActivity.class));
            finish();
        });

        faqItem.setOnClickListener(v -> {
            startActivity(new Intent(MenuActivity.this, FaqListActivity.class));
            finish();
        });
    }
}
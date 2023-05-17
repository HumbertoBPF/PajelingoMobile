package com.example.pajelingo.activities;

import static com.example.pajelingo.utils.SharedPreferences.isUserAuthenticated;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pajelingo.R;
import com.example.pajelingo.activities.account.MyProfileActivity;
import com.example.pajelingo.activities.account.SearchAccountActivity;
import com.example.pajelingo.adapters.MenuAdapter;
import com.example.pajelingo.models.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity {
    private RecyclerView menuRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        menuRecyclerView = findViewById(R.id.menu_recycler_view);

        List<MenuItem> items = new ArrayList<>();

        if (isUserAuthenticated(MenuActivity.this)){
            items.add(new MenuItem(getString(R.string.profile_menu_item_title), null, R.drawable.ic_profile, view -> {
                startActivity(new Intent(MenuActivity.this, MyProfileActivity.class));
                finish();
            }));
        }

        items.add(new MenuItem(getString(R.string.search_account_menu_item_title), null, R.drawable.ic_search_account, view -> {
            startActivity(new Intent(MenuActivity.this, SearchAccountActivity.class));
            finish();
        }));

        items.add(new MenuItem(getString(R.string.rankings_menu_item_title), null, R.drawable.ic_rankings, view -> {
            startActivity(new Intent(MenuActivity.this, RankingActivity.class));
            finish();
        }));

        items.add(new MenuItem(getString(R.string.faq_menu_item_title), null, R.drawable.ic_faq, view -> {
            startActivity(new Intent(MenuActivity.this, FaqListActivity.class));
            finish();
        }));

        menuRecyclerView.setAdapter(new MenuAdapter(items));
    }
}
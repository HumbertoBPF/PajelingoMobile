package com.example.pajelingo.activities;

import static com.example.pajelingo.utils.Tools.isUserAuthenticated;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pajelingo.R;
import com.example.pajelingo.activities.account.ProfileActivity;
import com.example.pajelingo.adapters.MenuItemAdapter;
import com.example.pajelingo.models.ui.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity {
    private RecyclerView menuItemsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        menuItemsRecyclerView = findViewById(R.id.menu_items_recycler_view);

        List<MenuItem> menuItems = new ArrayList<>();

        if (isUserAuthenticated(MenuActivity.this)){
            menuItems.add(new MenuItem(R.drawable.ic_profile, getString(R.string.profile_menu_item_title), null, v -> {
                startActivity(new Intent(MenuActivity.this, ProfileActivity.class));
                finish();
            }));
        }

        menuItems.add(new MenuItem(R.drawable.ic_rankings, getString(R.string.rankings_menu_item_title), null, v -> {
            startActivity(new Intent(MenuActivity.this, RankingActivity.class));
            finish();
        }));

        menuItems.add(new MenuItem(R.drawable.ic_faq, getString(R.string.faq_menu_item_title), null, v -> {
            startActivity(new Intent(MenuActivity.this, FaqListActivity.class));
            finish();
        }));

        menuItemsRecyclerView.setAdapter(new MenuItemAdapter(menuItems));
    }
}
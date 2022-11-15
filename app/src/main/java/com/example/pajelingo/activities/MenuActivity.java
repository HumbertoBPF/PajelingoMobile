package com.example.pajelingo.activities;

import static com.example.pajelingo.utils.Tools.isUserAuthenticated;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pajelingo.R;
import com.example.pajelingo.adapters.MenuItemAdapter;
import com.example.pajelingo.models.MenuItem;

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
            menuItems.add(new MenuItem(R.drawable.ic_profile, "Profile", null, null));
        }

        menuItems.add(new MenuItem(R.drawable.ic_rankings, "Rankings", null,
                v -> startActivity(new Intent(MenuActivity.this, RankingActivity.class))));

        menuItems.add(new MenuItem(R.drawable.ic_faq, "FAQ", null, null));

        menuItemsRecyclerView.setAdapter(new MenuItemAdapter(menuItems));
    }
}
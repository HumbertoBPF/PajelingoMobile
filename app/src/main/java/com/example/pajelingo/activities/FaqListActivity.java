package com.example.pajelingo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pajelingo.R;
import com.example.pajelingo.adapters.MenuAdapter;
import com.example.pajelingo.models.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class FaqListActivity extends AppCompatActivity {
    private RecyclerView faqRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq_list);

        setTitle(getString(R.string.faq_activity_title));

        faqRecyclerView = findViewById(R.id.faq_recycler_view);

        List<MenuItem> items = new ArrayList<>();

        items.add(getMenuItem(R.string.faq_1_title, R.string.faq_1_description));
        items.add(getMenuItem(R.string.faq_2_title, R.string.faq_2_description));
        items.add(getMenuItem(R.string.faq_3_title, R.string.faq_3_description));
        items.add(getMenuItem(R.string.faq_4_title, R.string.faq_4_description));

        faqRecyclerView.setAdapter(new MenuAdapter(items));
    }

    private MenuItem getMenuItem(int titleResource, int descriptionResource) {
        String title = getString(titleResource);
        String description = getString(descriptionResource);

        return new MenuItem(title, description, null, (View.OnClickListener) view -> {
            Intent intent = new Intent(FaqListActivity.this, FaqActivity.class);
            intent.putExtra("question", title);
            intent.putExtra("answer", description);
            startActivity(intent);
        });
    }
}
package com.example.pajelingo.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.pajelingo.R;
import com.example.pajelingo.adapters.MenuItemAdapter;
import com.example.pajelingo.models.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class FaqListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq_list);

        setTitle(getString(R.string.faq_activity_title));

        recyclerView = findViewById(R.id.faq_recycler_view);

        List<MenuItem> faqItems = new ArrayList<>();

        faqItems.add(faqItemFactory(getString(R.string.faq_1_title), getString(R.string.faq_1_description)));
        faqItems.add(faqItemFactory(getString(R.string.faq_2_title), getString(R.string.faq_2_description)));
        faqItems.add(faqItemFactory(getString(R.string.faq_3_title), getString(R.string.faq_3_description)));
        faqItems.add(faqItemFactory(getString(R.string.faq_4_title), getString(R.string.faq_4_description)));

        recyclerView.setAdapter(new MenuItemAdapter(faqItems));
    }

    private MenuItem faqItemFactory(String title, String description){
        return new MenuItem(null, title, description, v -> {
            Intent intent = new Intent(FaqListActivity.this, FaqActivity.class);
            intent.putExtra("question", title);
            intent.putExtra("answer", description);
            startActivity(intent);
        });
    }
}
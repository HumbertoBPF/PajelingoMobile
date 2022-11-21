package com.example.pajelingo.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pajelingo.R;
import com.example.pajelingo.ui.SimpleListItem;

public class FaqListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq_list);

        setTitle(getString(R.string.faq_activity_title));

        SimpleListItem[] faqItems = {
                findViewById(R.id.faq_1),
                findViewById(R.id.faq_2),
                findViewById(R.id.faq_3),
                findViewById(R.id.faq_4)
        };

        for (SimpleListItem faqItem: faqItems){
            faqItem.setOnClickListener(v -> {
                Intent intent = new Intent(FaqListActivity.this, FaqActivity.class);
                intent.putExtra("question", faqItem.getTitle());
                intent.putExtra("answer", faqItem.getDescription());
                startActivity(intent);
            });
        }
    }
}
package com.example.pajelingo.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.pajelingo.R;

public class FaqActivity extends AppCompatActivity {
    private TextView questionTextView;
    private TextView answerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        setTitle(getString(R.string.faq_activity_title));

        Intent intent = getIntent();

        String question = intent.getStringExtra("question");
        String answer = intent.getStringExtra("answer");

        questionTextView = findViewById(R.id.question_text_view);
        answerTextView = findViewById(R.id.answer_text_view);

        questionTextView.setText(question);
        answerTextView.setText(answer);
    }
}
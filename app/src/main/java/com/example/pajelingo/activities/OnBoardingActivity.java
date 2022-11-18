package com.example.pajelingo.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pajelingo.R;
import com.example.pajelingo.models.ui.OnBoardingItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class OnBoardingActivity extends AppCompatActivity {

    private ImageView onBoardingImageView;
    private TextView onBoardingTextView;
    private FloatingActionButton nextButton;
    private FloatingActionButton backButton;
    private Button getStartedButton;

    private final List<OnBoardingItem> items = new ArrayList<>();
    private int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

        onBoardingImageView = findViewById(R.id.on_boarding_image_view);
        onBoardingTextView = findViewById(R.id.on_boarding_text_view);
        nextButton = findViewById(R.id.next_button);
        backButton = findViewById(R.id.back_button);
        getStartedButton = findViewById(R.id.get_started_button);

        items.add(new OnBoardingItem(R.drawable.on_boarding_1, getString(R.string.on_boarding_1)));
        items.add(new OnBoardingItem(R.drawable.on_boarding_2, getString(R.string.on_boarding_2)));
        items.add(new OnBoardingItem(R.drawable.on_boarding_3, getString(R.string.on_boarding_3)));

        bind();

        getStartedButton.setOnClickListener(v -> {
            SharedPreferences sp = getSharedPreferences(getString(R.string.sp_file_name),MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(getString(R.string.is_first_access), false);
            editor.apply();

            startActivity(new Intent(OnBoardingActivity.this, MainActivity.class));
            finish();
        });

        backButton.setOnClickListener(v -> {
            if (currentIndex > 0){
                currentIndex--;
                bind();
            }
        });

        nextButton.setOnClickListener(v -> {
            if (currentIndex < items.size() - 1){
                currentIndex++;
                bind();
            }
        });
    }

    private void bind(){
        updateButton(nextButton,currentIndex < items.size() - 1);
        updateButton(backButton,currentIndex > 0);

        if (currentIndex == items.size() - 1){
            getStartedButton.setVisibility(View.VISIBLE);
        }else{
            getStartedButton.setVisibility(View.GONE);
        }

        OnBoardingItem currentItem = items.get(currentIndex);

        onBoardingImageView.setImageResource(currentItem.getImageResource());
        onBoardingTextView.setText(currentItem.getDescription());
    }

    private void updateButton(FloatingActionButton button, boolean isEnabled) {
        int colorResource = isEnabled?R.color.blue:R.color.gray;
        int color = getResources().getColor(colorResource);
        button.setBackgroundTintList(ColorStateList.valueOf(color));
        button.setEnabled(isEnabled);
    }
}
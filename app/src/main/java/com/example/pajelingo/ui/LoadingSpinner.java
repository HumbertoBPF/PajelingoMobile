package com.example.pajelingo.ui;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.pajelingo.R;

public class LoadingSpinner extends ConstraintLayout {
    public LoadingSpinner(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.loading_spinner_layout, this);
    }
}

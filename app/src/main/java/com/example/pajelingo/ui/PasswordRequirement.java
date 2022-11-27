package com.example.pajelingo.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.pajelingo.R;

public class PasswordRequirement extends ConstraintLayout {

    private TextView requirementTextView;
    private ImageView requirementImageView;

    public PasswordRequirement(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.password_requirement_layout, this);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PasswordRequirement, 0, 0);
        CharSequence text = typedArray.getText(R.styleable.PasswordRequirement_requirementText);
        boolean isChecked = typedArray.getBoolean(R.styleable.PasswordRequirement_isChecked, false);
        typedArray.recycle();

        initComponents();

        setText(text);
        setChecked(isChecked);
    }

    private void initComponents() {
        requirementTextView = findViewById(R.id.requirement_text_view);
        requirementImageView = findViewById(R.id.requirement_image_view);
    }

    public void setText(CharSequence text) {
        requirementTextView.setText(text);
    }

    public CharSequence getText(){
        return requirementTextView.getText();
    }

    public void setChecked(boolean isChecked) {
        if (!isChecked) {
            requirementImageView.setVisibility(GONE);
        } else {
            requirementImageView.setVisibility(VISIBLE);
        }
    }

    public boolean isChecked() {
        return requirementImageView.getVisibility() == VISIBLE;
    }
}
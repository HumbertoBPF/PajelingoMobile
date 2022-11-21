package com.example.pajelingo.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.pajelingo.R;

public class SimpleListItem extends ConstraintLayout {

    private CharSequence title;
    private CharSequence description;

    private TextView titleTextView;
    private TextView descriptionTextView;
    private ImageView iconImageView;

    public SimpleListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.simple_list_item_layout, this);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SimpleListItem, 0, 0);
        CharSequence title = typedArray.getText(R.styleable.SimpleListItem_title);
        CharSequence description = typedArray.getText(R.styleable.SimpleListItem_description);
        int resourceId = typedArray.getResourceId(R.styleable.SimpleListItem_icon, -1);
        typedArray.recycle();

        initComponents();

        setTitle(title);
        setDescription(description);
        setIcon(resourceId);
    }

    private void initComponents() {
        titleTextView = findViewById(R.id.title_text_view);
        descriptionTextView = findViewById(R.id.description_text_view);
        iconImageView = findViewById(R.id.icon_image_view);
    }

    public CharSequence getTitle(){
        return title;
    }

    public CharSequence getDescription(){
        return description;
    }

    public void setTitle(CharSequence title){
        this.title = title;

        if (title != null){
            titleTextView.setText(title);
            titleTextView.setVisibility(VISIBLE);
        }else{
            titleTextView.setVisibility(GONE);
        }
    }

    public void setDescription(CharSequence description){
        this.description = description;

        String descriptionString = String.valueOf(description);

        if (description != null){
            descriptionTextView.setVisibility(VISIBLE);
            if (description.length() > 100){
                descriptionTextView.setText(descriptionString.substring(0, 97)+"...");
            }else{
                descriptionTextView.setText(descriptionString);
            }
        }else{
            descriptionTextView.setVisibility(GONE);
        }
    }

    public void setIcon(int resourceId){
        if (resourceId != -1){
            iconImageView.setImageResource(resourceId);
            iconImageView.setVisibility(VISIBLE);
        }else{
            iconImageView.setVisibility(GONE);
        }
    }
}

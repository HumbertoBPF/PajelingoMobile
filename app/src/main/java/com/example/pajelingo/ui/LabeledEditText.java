package com.example.pajelingo.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.EditText;

import androidx.constraintlayout.widget.Guideline;

import com.example.pajelingo.R;

public class LabeledEditText extends LabeledView {
    private Guideline guideline;
    private EditText fieldInputEditText;

    public LabeledEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.labeled_input_layout, this);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LabeledEditText, 0, 0);
        labelText = typedArray.getText(R.styleable.LabeledEditText_label);
        float labelBias = typedArray.getFloat(R.styleable.LabeledEditText_labelBias, 0.25f);
        boolean labelBold = typedArray.getBoolean(R.styleable.LabeledEditText_labelBold, true);
        boolean centerText = typedArray.getBoolean(R.styleable.LabeledEditText_centerText, false);
        typedArray.recycle();

        initComponents();

        labelTextView.setText(labelText);

        if (labelBold){
            labelTextView.setTypeface(labelTextView.getTypeface(), Typeface.BOLD);
        }else{
            labelTextView.setTypeface(null, Typeface.NORMAL);
        }

        guideline.setGuidelinePercent(labelBias);

        if (centerText){
            labelTextView.setGravity(Gravity.CENTER);
            fieldInputEditText.setGravity(Gravity.CENTER);
        }else{
            labelTextView.setGravity(Gravity.NO_GRAVITY);
            fieldInputEditText.setGravity(Gravity.NO_GRAVITY);
        }
    }

    private void initComponents() {
        guideline = findViewById(R.id.guideline);
        labelTextView = findViewById(R.id.field_label_text_view);
        fieldInputEditText = findViewById(R.id.field_input_edit_text);
    }

    public void setInput(CharSequence input){
        fieldInputEditText.setText(input);
    }

    public CharSequence getInput(){
        return fieldInputEditText.getText();
    }
}

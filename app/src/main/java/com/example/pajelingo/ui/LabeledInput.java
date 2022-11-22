package com.example.pajelingo.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;

import com.example.pajelingo.R;

public class LabeledInput extends ConstraintLayout {
    private CharSequence labelText;
    private float labelBias;
    private boolean labelBold;
    private boolean centerText;

    private Guideline guideline;
    private TextView fieldLabelTextView;
    private EditText fieldInputEditText;

    public LabeledInput(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.labeled_input_layout, this);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LabeledInput, 0, 0);
        labelText = typedArray.getText(R.styleable.LabeledInput_label);
        labelBias = typedArray.getFloat(R.styleable.LabeledInput_labelBias, 0.25f);
        labelBold = typedArray.getBoolean(R.styleable.LabeledInput_labelBold, true);
        centerText = typedArray.getBoolean(R.styleable.LabeledInput_centerText, false);
        typedArray.recycle();

        initComponents();

        fieldLabelTextView.setText(labelText);

        if (labelBold){
            fieldLabelTextView.setTypeface(fieldLabelTextView.getTypeface(), Typeface.BOLD);
        }else{
            fieldLabelTextView.setTypeface(null, Typeface.NORMAL);
        }

        guideline.setGuidelinePercent(labelBias);

        if (centerText){
            fieldLabelTextView.setGravity(Gravity.CENTER);
            fieldInputEditText.setGravity(Gravity.CENTER);
        }else{
            fieldLabelTextView.setGravity(Gravity.NO_GRAVITY);
            fieldInputEditText.setGravity(Gravity.NO_GRAVITY);
        }
    }

    private void initComponents() {
        guideline = findViewById(R.id.guideline);
        fieldLabelTextView = findViewById(R.id.field_label_text_view);
        fieldInputEditText = findViewById(R.id.field_input_edit_text);
    }

    public void setLabel(CharSequence label){
        labelText = label;
        fieldLabelTextView.setText(label);
    }

    public void setInput(CharSequence input){
        fieldInputEditText.setText(input);
    }

    public CharSequence getLabel(){
        return fieldLabelTextView.getText().toString();
    }

    public CharSequence getInput(){
        return fieldInputEditText.getText();
    }
}

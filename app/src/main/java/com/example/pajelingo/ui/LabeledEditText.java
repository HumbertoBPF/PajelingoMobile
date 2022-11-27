package com.example.pajelingo.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.EditText;

import androidx.constraintlayout.widget.Guideline;

import com.example.pajelingo.R;

public class LabeledEditText extends LabeledView {
    private Guideline guideline;
    private EditText editText;

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
        int type = typedArray.getInteger(R.styleable.LabeledEditText_type,0);
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
            editText.setGravity(Gravity.CENTER);
        }else{
            labelTextView.setGravity(Gravity.NO_GRAVITY);
            editText.setGravity(Gravity.NO_GRAVITY);
        }

        switch (type){
            case 1:
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                break;
            case 2:
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                break;
        }
    }

    private void initComponents() {
        guideline = findViewById(R.id.guideline);
        labelTextView = findViewById(R.id.label);
        editText = findViewById(R.id.edit_text);
    }

    public EditText getEditText() {
        return editText;
    }
}

package com.example.pajelingo.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Spinner;

import com.example.pajelingo.R;

public class LabeledSpinner extends LabeledView {
    private Spinner spinner;

    public LabeledSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.labeled_spinner_layout, this);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LabeledSpinner, 0, 0);
        CharSequence labelText = typedArray.getText(R.styleable.LabeledSpinner_spinnerLabel);
        typedArray.recycle();

        initComponents();

        labelTextView.setText(labelText);
    }

    private void initComponents() {
        labelTextView = findViewById(R.id.label);
        spinner = findViewById(R.id.spinner);
    }

    public Spinner getSpinner(){
        return spinner;
    }
}

package com.example.pajelingo.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

public abstract class LabeledView extends ConstraintLayout {
    protected CharSequence labelText;
    protected TextView labelTextView;

    public LabeledView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setLabel(CharSequence label){
        labelText = label;
        labelTextView.setText(label);
    }

    public CharSequence getLabel(){
        return labelTextView.getText().toString();
    }

}

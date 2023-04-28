package com.example.pajelingo.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.pajelingo.R;
import com.google.android.material.button.MaterialButton;

public class LoadingButton extends ConstraintLayout {
    private final MaterialButton loadingButton;
    private CharSequence text;
    private OnClickListener onClickListener = null;
    private final int icon;

    public LoadingButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.loading_button_layout, this);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LoadingButton, 0,0);

        text = typedArray.getText(R.styleable.LoadingButton_loadingButtonText);
        icon = typedArray.getInt(R.styleable.LoadingButton_loadingButtonIcon, -1);
        typedArray.recycle();

        loadingButton = findViewById(R.id.loading_button);

        setLoading(false);
    }

    public void setLoading(boolean isLoading) {
        if (isLoading) {
            loadingButton.setText(R.string.loading_button_text);
            loadingButton.setIconResource(R.drawable.ic_hourglass);
            loadingButton.setEnabled(false);
        }else{
            loadingButton.setText(text);
            if (icon != -1) {
                loadingButton.setIconResource(icon);
            }else {
                loadingButton.setIcon(null);
            }
            loadingButton.setEnabled(true);
            loadingButton.setOnClickListener(this.onClickListener);
        }
    }

    public void setText(CharSequence text) {
        this.text = text;
        loadingButton.setText(this.text);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        loadingButton.setOnClickListener(onClickListener);
    }
}

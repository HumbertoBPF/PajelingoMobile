package com.example.pajelingo.models;

import android.view.View;

public class MenuItem {
    private final String title;
    private final String description;
    private final Integer icon;
    private final View.OnClickListener onClickListener;

    public MenuItem(String title, String description, Integer icon, View.OnClickListener onClickListener) {
        this.title = title;
        this.description = description;
        this.icon = icon;
        this.onClickListener = onClickListener;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Integer getIcon() {
        return icon;
    }

    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }
}

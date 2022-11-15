package com.example.pajelingo.models;

import android.view.View;

public class MenuItem {
    private final Integer iconResource;
    private final String title;
    private final String description;
    private final View.OnClickListener onClickListener;

    public MenuItem(Integer iconResource, String title, String description, View.OnClickListener onClickListener) {
        this.iconResource = iconResource;
        this.title = title;
        this.description = description;
        this.onClickListener = onClickListener;
    }

    public Integer getIconResource() {
        return iconResource;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }
}

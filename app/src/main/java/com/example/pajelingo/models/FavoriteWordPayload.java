package com.example.pajelingo.models;

import com.google.gson.annotations.SerializedName;

public class FavoriteWordPayload {
    @SerializedName("is_favorite")
    private Boolean isFavorite;

    public FavoriteWordPayload(Boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public Boolean getFavorite() {
        return isFavorite;
    }

    public void setFavorite(Boolean favorite) {
        isFavorite = favorite;
    }
}

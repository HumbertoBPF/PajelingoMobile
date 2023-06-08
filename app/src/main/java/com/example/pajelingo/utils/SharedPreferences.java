package com.example.pajelingo.utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;

import com.example.pajelingo.R;
import com.example.pajelingo.models.Badge;
import com.example.pajelingo.models.Token;
import com.example.pajelingo.models.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SharedPreferences {
    /**
     * Verifies if the user credentials are saved on Shared Preferences, which indicates that the user
     * is authenticated.
     * @param context application Context
     * @return if the user credentials are saved on Shared Preferences
     */
    public static boolean isUserAuthenticated(Context context){
        android.content.SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.sp_file_name),MODE_PRIVATE);
        String username = sp.getString(context.getString(R.string.username_sp),null);
        String email = sp.getString(context.getString(R.string.email_sp),null);
        String token = sp.getString(context.getString(R.string.token_sp),null);

        return (username != null) && (email != null) && (token != null);
    }

    /**
     * Saves the specified user token on Shared Preferences.
     * @param context application Context
     * @param token token that must be saved
     */
    public static void saveToken(Context context, Token token){
        android.content.SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.sp_file_name),MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = sp.edit();
        editor.putString(context.getString(R.string.token_sp), token.getToken());
        editor.apply();
    }

    /**
     * Saves the specified user credentials on Shared Preferences.
     * @param context application Context
     * @param user User whose credentials must be saved
     */
    public static void saveUserData(Context context, User user) {
        android.content.SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.sp_file_name),MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = sp.edit();
        editor.putString(context.getString(R.string.username_sp), user.getUsername());
        editor.putString(context.getString(R.string.email_sp), user.getEmail());
        editor.putString(context.getString(R.string.picture_sp), user.getPicture());
        editor.putString(context.getString(R.string.bio_sp), user.getBio());
        editor.putString(context.getString(R.string.badges_sp), new Gson().toJson(user.getBadges()));
        editor.apply();
    }

    /**
     * Deletes the user credentials from Shared Preferences.
     * @param context application Context
     */
    public static void deleteUserData(Context context) {
        android.content.SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.sp_file_name),MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = sp.edit();
        // Remove user from Shared Preferences due to logout
        editor.remove(context.getString(R.string.token_sp));
        editor.remove(context.getString(R.string.username_sp));
        editor.remove(context.getString(R.string.email_sp));
        editor.remove(context.getString(R.string.picture_sp));
        editor.remove(context.getString(R.string.bio_sp));
        editor.remove(context.getString(R.string.badges_sp));
        editor.apply();
    }

    /**
     * Generates a Token Authentication string to be provided as authorization header.
     * @return Basic Authentication token
     */
    public static String getAuthToken(Context context) {
        android.content.SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.sp_file_name), MODE_PRIVATE);
        String token = sp.getString(context.getString(R.string.token_sp), "");
        return "Token " + token;
    }

    public static List<Badge> getBadgesFromSharedPreferences(Context context, android.content.SharedPreferences sp) {
        String badgesJson = sp.getString(context.getString(R.string.badges_sp), null);

        if (badgesJson != null) {
            Type type = new TypeToken<ArrayList<Badge>>() {}.getType();
            return new Gson().fromJson(badgesJson, type);
        }

        return new ArrayList<>();
    }
}

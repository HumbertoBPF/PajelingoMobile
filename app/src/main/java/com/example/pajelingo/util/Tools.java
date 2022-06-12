package com.example.pajelingo.util;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.example.pajelingo.R;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;

public class Tools{

    /**
     * Picks a random item from a list.
     * @param list list from which the item is going to be extracted.
     * @param <E> same generic type as the list.
     * @return A random item from the list.
     */
    public static <E> E getRandomItemFromList(List<E> list){
        Random rand = new Random();
        int randomIndex = rand.nextInt(list.size());

        return list.get(randomIndex);
    }

    public static boolean isUserAuthenticated(Context context){
        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.sp_file_name),MODE_PRIVATE);
        String username = sp.getString(context.getString(R.string.username_sp),null);
        String password = sp.getString(context.getString(R.string.password_sp),null);

        return (username != null) && (password != null);
    }

    public static String getAuthToken(String username, String password) {
        byte[] data = (username + ":" + password).getBytes(StandardCharsets.UTF_8);
        return "Basic " + Base64.encodeToString(data, Base64.NO_WRAP);
    }

}

package com.example.pajelingo.utils;

import android.content.Context;
import android.widget.Toast;

import com.example.pajelingo.R;
import com.example.pajelingo.models.Word;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Tools{

    /**
     * Picks a random item from a list.
     * @param list list from which the item is going to be extracted
     * @param <E> same generic type as the list
     * @return A random item from the list
     */
    public static <E> E getRandomItemFromList(List<E> list){
        Random rand = new Random();
        int randomIndex = rand.nextInt(list.size());

        return list.get(randomIndex);
    }

    /**
     * Validates a password string. A password must fulfill the following requirements:
     * <br><br>
     * - It must have at least one digit<br>
     * - It must have at least one letter<br>
     * - It must have at least one special character<br>
     * - Its length must be between 8 and 30<br>
     * <br>
     * @param password password to be validated
     * @return a HashMap with 4 String keys: hasDigit, hasLetter, hasSpecialCharacter, hasValidLength.
     * Each key has a boolean value indicating if the requirements mentioned above are fulfilled.
     */
    public static HashMap<String, Boolean> validatePassword(String password){
        int inputLength = password.length();

        HashMap<String, Boolean> passwordMap = new HashMap<>();
        passwordMap.put("hasDigit", false);
        passwordMap.put("hasLetter", false);
        passwordMap.put("hasSpecialCharacter", false);
        passwordMap.put("hasValidLength", ((inputLength >= 8) && (inputLength <= 30)));

        for (int i = 0;i < inputLength;i++){
            char c = password.charAt(i);

            if (Character.isDigit(c)){
                passwordMap.put("hasDigit", true);
            }

            if (Character.isLetter(c)){
                passwordMap.put("hasLetter", true);
            }

            if (!Character.isDigit(c) && !Character.isLetter(c) && !Character.isWhitespace(c)){
                passwordMap.put("hasSpecialCharacter", true);
            }

            if (Boolean.TRUE.equals(passwordMap.get("hasDigit")) &&
                    Boolean.TRUE.equals(passwordMap.get("hasLetter")) &&
                    Boolean.TRUE.equals(passwordMap.get("hasSpecialCharacter"))){
                break;
            }
        }

        return passwordMap;
    }

    /**
     * Displays a toast with an error message communicating an error when toggling the favorite status of a word.
     * @param context application's context
     * @param word word concerned by the operation
     */
    public static void displayFavoriteWordError(Context context, Word word) {
        if (word.getFavorite()) {
            Toast.makeText(context, R.string.error_removing_word_from_favorites, Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, R.string.error_adding_word_to_favorites, Toast.LENGTH_SHORT).show();
        }
    }

}

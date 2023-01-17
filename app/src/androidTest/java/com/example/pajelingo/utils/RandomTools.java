package com.example.pajelingo.utils;

import android.content.Context;

import com.example.pajelingo.daos.LanguageDao;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Language;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class RandomTools {
    /**
     * Generates a random string composed of lowercase letters.
     * @param length length of the string
     * @param hasLetters boolean indicating if letters are allowed
     * @param hasDigits boolean indicating if digits are allowed
     * @param hasSpecialCharacters boolean indicating if letters are allowed
     * @return a random string with the specified length
     */
    public static String getRandomString(int length, boolean hasLetters, boolean hasDigits, boolean hasSpecialCharacters){
        String LETTERS = "abcdefghijklmnopqrstuvxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String DIGITS = "12345678";
        String SPECIAL_CHARACTERS = "-+*/=)(!:;,$";

        String validCharacters = "";

        if (hasLetters){
            validCharacters += LETTERS;
        }

        if (hasDigits){
            validCharacters += DIGITS;
        }

        if (hasSpecialCharacters){
            validCharacters += SPECIAL_CHARACTERS;
        }

        if (validCharacters.length() == 0){
            throw new RuntimeException("Your string needs to have at least one of the three sets of characters (letters, digits or special characters)");
        }

        String randomString = "";

        for (int i=0;i<length;i++){
            int randomIndex = new Random().nextInt(validCharacters.length());
            randomString += validCharacters.charAt(randomIndex);
        }

        return randomString;
    }

    public static String getRandomWord(int length){
        return getRandomString(length, true, false, false);
    }

    /**
     * Generates a random integer in the interval [lowerBound, upperBound]
     * @param lowerBound lower bound of the interval (included)
     * @param upperBound upper bound of the interval (included)
     * @return a random integer in the specified interval
     */
    public static int getRandomInteger(int lowerBound, int upperBound){
        return lowerBound + new Random().nextInt(upperBound - lowerBound + 1);
    }

    /**
     * Returns a random language from the app database. Languages that must be excluded from being
     * picked can be specified as varargs.
     * @param context application Context
     * @param excludedLanguages name of the languages that must be excluded
     * @return random language in the app database obeying the constraints
     */
    public static Language getRandomLanguage(Context context, String... excludedLanguages){
        LanguageDao languageDao = AppDatabase.getInstance(context).getLanguageDao();

        List<Language> languages = new ArrayList<>();
        HashMap<String, Boolean> mapExcludedLanguages = new HashMap<>();
        // HashMap to have linear time complexity
        for (String excludedLanguage: excludedLanguages){
            mapExcludedLanguages.put(excludedLanguage, true);
        }
        // Consider all the languages, except the excluded languages specified as varargs
        for (Language language: languageDao.getAllRecords()){
            if (mapExcludedLanguages.get(language.getLanguageName()) == null){
                languages.add(language);
            }
        }
        // If there is no language fulfilling the constraints, return null
        if (languages.size() == 0) {
            return null;
        }
        // Otherwise, return a random language
        return languages.get(getRandomInteger(0, languages.size() - 1));
    }

    /**
     * Gets an <b>invalid</b> password. To get an invalid password one of the requirements below must be false:
     * <br><br>
     * - The length of the password must be between 8 and 30.<br>
     * - The password must have one letter.<br>
     * - The password must have one digit.<br>
     * - The password must have one special character.<br>
     * <br>
     * If all the requirements above are true, an invalid password cannot be generated and an exception is raised.
     * @param length length of the password.
     * @param hasLetter boolean indicating if the password has letters.
     * @param hasDigit boolean indicating if the password has digits.
     * @param hasSpecialCharacter boolean indicating if the password has special characters.
     * @return an invalid password as defined above.
     */
    public static String getRandomInvalidPassword(int length, boolean hasLetter, boolean hasDigit, boolean hasSpecialCharacter){
        boolean hasValidLength = ((length >= 8) && (length<=30));

        if (hasDigit && hasLetter && hasSpecialCharacter && hasValidLength){
            throw new RuntimeException("It seems that the password you have requested is valid...");
        }

        String password = "";

        if (length == 0){
            return password;
        }

        if (hasLetter){
            password += getRandomString(1, true, false, false);
        }

        if (hasDigit){
            password += getRandomString(1, false, true, false);
        }

        if (hasSpecialCharacter){
            password += getRandomString(1, false, false, true);
        }

        if (length < password.length()){
            throw new RuntimeException("You requested a password whose constraints are not compatible with the specified length ("+length+")");
        }

        return password + getRandomString(length - password.length(), hasLetter, hasDigit, hasSpecialCharacter);
    }

    public static String getRandomEmail(){
        return getRandomWord(10) + "@test.com";
    }

    public static String getRandomUsername(){
        return getRandomWord(10);
    }

    public static String getRandomValidPassword(){
        return getRandomString(getRandomInteger(5, 27), true, true, true) + "1@a";
    }
}

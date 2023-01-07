package com.example.pajelingo.utils;

import static android.content.Context.MODE_PRIVATE;

import static com.example.pajelingo.database.settings.AppDatabase.NAME_DB;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import androidx.appcompat.app.AlertDialog;

import com.example.pajelingo.R;
import com.example.pajelingo.activities.MainActivity;
import com.example.pajelingo.models.User;
import com.example.pajelingo.synchronization.ArticleSynchro;

import java.io.File;
import java.nio.charset.StandardCharsets;
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
     * Verifies if the user credentials are saved on Shared Preferences, which indicates that the user
     * is authenticated.
     * @param context application Context
     * @return if the user credentials are saved on Shared Preferences
     */
    public static boolean isUserAuthenticated(Context context){
        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.sp_file_name),MODE_PRIVATE);
        String username = sp.getString(context.getString(R.string.username_sp),null);
        String email = sp.getString(context.getString(R.string.email_sp),null);
        String password = sp.getString(context.getString(R.string.password_sp),null);

        return (username != null) && (email != null) && (password != null);
    }

    /**
     * Saves the specified user credentials on Shared Preferences.
     * @param context application Context
     * @param user User whose credentials must be saved
     */
    public static void saveStateAndUserCredentials(Context context, User user) {
        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.sp_file_name),MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(context.getString(R.string.username_sp), user.getUsername());
        editor.putString(context.getString(R.string.email_sp), user.getEmail());
        editor.putString(context.getString(R.string.password_sp), user.getPassword());
        editor.putString(context.getString(R.string.picture_sp), user.getPicture());
        editor.apply();
    }

    /**
     * Deletes the user credentials from Shared Preferences.
     * @param context application Context
     */
    public static void deleteUserCredentials(Context context) {
        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.sp_file_name),MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        // Remove user from Shared Preferences due to logout
        editor.remove(context.getString(R.string.username_sp));
        editor.remove(context.getString(R.string.email_sp));
        editor.remove(context.getString(R.string.password_sp));
        editor.apply();
    }

    /**
     * Generates a Basic Authentication token corresponding to the specified credentials.
     * @param username username credential
     * @param password password credential
     * @return Basic Authentication token
     */
    public static String getAuthToken(String username, String password) {
        byte[] data = (username + ":" + password).getBytes(StandardCharsets.UTF_8);
        return "Basic " + Base64.encodeToString(data, Base64.NO_WRAP);
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
     * Deletes the content of the specified folder, that is its sub folders and files.
     * @param file File instance corresponding to the file whose content must be deleted.
     */
    public static void clearFolder(File file){
        // Specified file can't be null
        if (file == null){
            return;
        }

        File[] subFiles = file.listFiles();
        // Check if the file has subFiles
        if (subFiles != null) {
            for (File subFile: subFiles){
                clearFolder(subFile);
                subFile.delete();
            }
        }
    }

    /**
     * Launches the synchronization of resources, which is implemented through a chain of
     * responsibility design pattern.
     * @param context application's context.
     * @param dialog AlertDialog instance that must be launched while the synchronization takes
     *               place.
     */
    public static void launchSynchroResources(Context context, AlertDialog dialog){
        // Delete database and internal storage files before synchronization
        context.deleteDatabase(NAME_DB);
        clearFolder(context.getFilesDir());
        new ArticleSynchro(context, dialog).execute();
    }

    /**
     * Gets a Bitmap from a base 64 string. The Bitmap instance can be used, for instance, to
     * display or save the picture as a file.
     * @param base64String base 64 string encoding a picture.
     * @return a Bitmap instance representing the encoded picture.
     */
    public static Bitmap getPictureFromBase64String(String base64String){
        byte[] bytes = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0, bytes.length);
    }
}

package com.example.pajelingo.utils;

import java.util.Random;

public class Tools {
    public static String getRandomString(int length){
        String validCharacters = "abcdefghijklmnopqrstuvxyz";
        String randomString = "";

        for (int i=0;i<length;i++){
            int randomIndex = new Random().nextInt(validCharacters.length());
            randomString += validCharacters.charAt(randomIndex);
        }

        return randomString;
    }
}

package com.example.pajelingo.utils;

import java.util.Random;

public class Tools {
    /**
     * Generates a random string composed of lowercase letters.
     * @param length length of the string
     * @return a random string with the specified length
     */
    public static String getRandomString(int length){
        String validCharacters = "abcdefghijklmnopqrstuvxyz";
        String randomString = "";

        for (int i=0;i<length;i++){
            int randomIndex = new Random().nextInt(validCharacters.length());
            randomString += validCharacters.charAt(randomIndex);
        }

        return randomString;
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
}

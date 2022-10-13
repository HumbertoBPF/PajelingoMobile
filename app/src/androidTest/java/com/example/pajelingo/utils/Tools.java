package com.example.pajelingo.utils;

import static com.example.pajelingo.util.Tools.getAuthToken;

import com.example.pajelingo.models.Language;
import com.example.pajelingo.models.Score;
import com.example.pajelingo.models.User;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelperTest;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import retrofit2.Response;

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

    /**
     * Verifies that the specified score of a user has a certain value.
     * @param user user owner of the score
     * @param language language of the score
     * @param game game of the score
     * @param value value to be asserted
     * @throws IOException thrown when some error related with HTTP communication occurs
     * @throws InterruptedException thrown when some error related with main thread manipulation occurs
     */
    public static void assertScoreValue(User user, Language language, String game, Long value) throws IOException, InterruptedException {
        Thread.sleep(3000);

        Response<List<Score>> responseScore =
                LanguageSchoolAPIHelperTest.getApiObject()
                        .getScore(getAuthToken(user.getUsername(), user.getPassword()), language.getId(), game).execute();
        List<Score> scores = responseScore.body();

        assert scores != null;
        if (value.equals(0L)){
            assert scores.size() == 0;
        }else{
            assert scores.size() == 1;
            assert scores.get(0).getScore().equals(value);
        }
    }
}

package com.example.pajelingo.utils;

import static com.example.pajelingo.utils.Tools.saveStateAndUserCredentials;
import static com.example.pajelingo.utils.Tools.saveToken;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;

import com.example.pajelingo.models.Conjugation;
import com.example.pajelingo.models.Language;
import com.example.pajelingo.models.Score;
import com.example.pajelingo.models.Token;
import com.example.pajelingo.models.User;
import com.example.pajelingo.models.Word;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;

import org.junit.Assert;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class TestTools {
    /**
     * Gets the score value matching the specified filters.
     * @param user user owner of the score
     * @param language language of the score
     * @param gameId id of the concerned game
     * @throws IOException thrown when some error related with HTTP communication occurs
     * @throws InterruptedException thrown when some error related with main thread manipulation occurs
     */
    public static Long getScore(User user, Language language, Long gameId) throws IOException, InterruptedException {
        Thread.sleep(3000);

        Response<Token> responseToken = LanguageSchoolAPIHelper.getApiObject().getToken(user).execute();
        Token token = responseToken.body();

        if (token == null) {
            Assert.fail("Failed to get user token");
        }

        Response<List<Score>> responseScore =
                LanguageSchoolAPIHelper.getApiObject().getScore("Token " + token.getToken(), language.getLanguageName(), gameId).execute();
        List<Score> scores = responseScore.body();

        assertNotNull(scores);

        if (scores.size() == 0){
            return 0L;
        }

        assertEquals(1, scores.size());
        return scores.get(0).getScore();
    }

    /**
     * Finds the conjugation of a verb in a certain tense given a list of verbs and a list of conjugations.
     * This method only returns the conjugation if:
     * <br><br>
     * -    The verb string is in the verb list.<br>
     * -    A conjugation whose tense matches the tense string and the verb id matches the verb found.
     * <br><br>
     * If no conjugation matches the criteria above, null is returned.
     * @param verbString verb of the desired conjugation
     * @param tenseString tense of the desired conjugation
     * @param verbs list of verbs available
     * @param conjugations list of conjugations available
     * @return the matched conjugation
     */
    public static Conjugation findConjugationOfVerb(String verbString, String tenseString,
                                                    List<Word> verbs, List<Conjugation> conjugations){
        Word verbObject = null;
        // Search a word matching the verb string in the specified verb list
        for (Word verb: verbs){
            if (verbString.equals(verb.getWordName())){
                verbObject = verb;
                break;
            }
        }

        if (verbObject == null){
            return null;
        }
        // Search a conjugation matching the conjugation string and the verb found in the specified conjugation list
        for (Conjugation conjugation: conjugations){
            if (verbObject.getId().equals(conjugation.getWordId()) && tenseString.equals(conjugation.getTense())){
                return conjugation;
            }
        }

        return null;
    }

    public static void authenticateUser(Context context, User user) throws IOException {
        Call<Token> tokenCall = LanguageSchoolAPIHelper.getApiObject().getToken(user);
        Response<Token> tokenResponse = tokenCall.execute();
        Token token = tokenResponse.body();

        if (token == null) {
            Assert.fail("Failed to get user token");
        }

        Call<User> userCall = LanguageSchoolAPIHelper.getApiObject().login("Token " + token.getToken());
        Response<User> userResponse = userCall.execute();

        if (userResponse.code() != 200){
            Assert.fail("Failed to create user.");
        }

        User responseUser = userResponse.body();

        if (responseUser == null){
            Assert.fail("No user object was returned.");
        }

        saveToken(context, token);
        saveStateAndUserCredentials(context, user);
    }
}

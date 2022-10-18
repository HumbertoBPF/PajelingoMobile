package com.example.pajelingo.utils;

import static com.example.pajelingo.util.Tools.getAuthToken;

import android.content.Context;

import com.example.pajelingo.daos.BaseDao;
import com.example.pajelingo.daos.LanguageDao;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Conjugation;
import com.example.pajelingo.models.Language;
import com.example.pajelingo.models.Score;
import com.example.pajelingo.models.User;
import com.example.pajelingo.models.Word;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelperTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
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

    /**
     * Saves the entities (list of records of a database) retrieved by an API call.
     * @param call Retrofit call object
     * @param dao Room Database DAO (Data Access Object)
     * @param <E> Entity class
     * @throws IOException raised when there is some error in the API HTTP request
     */
    public static <E> void saveEntitiesFromAPI(Call<List<E>> call, BaseDao<E> dao) throws IOException {
        Response<List<E>> response = call.execute();
        List<E> entities = response.body();
        dao.save(entities);
    }

    /**
     * Returns a random language from the app database. Languages that must be excluded from being
     * picked can be specified as varargs.
     * @param context application context
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
        Conjugation conjugationObject = null;
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

}

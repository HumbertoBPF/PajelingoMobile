package com.example.pajelingo.utils;

import static androidx.test.espresso.matcher.ViewMatchers.isRoot;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.test.espresso.PerformException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.util.HumanReadables;
import androidx.test.espresso.util.TreeIterables;

import com.example.pajelingo.R;
import com.example.pajelingo.daos.ArticleDao;
import com.example.pajelingo.daos.ConjugationDao;
import com.example.pajelingo.daos.WordDao;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Article;
import com.example.pajelingo.models.Conjugation;
import com.example.pajelingo.models.Language;
import com.example.pajelingo.models.Word;

import org.hamcrest.Matcher;

import java.util.List;
import java.util.concurrent.TimeoutException;

public class CustomViewActions {
    /**
     * Perform action of waiting until the element is accessible and shown.
     * @param viewMatcher A view matcher for the view to wait for.
     * @param millis The timeout of until when to wait for.
     * @param isVisible Boolean indicating the visibility that is awaited.
     */
    public static ViewAction waitForView(Matcher<View> viewMatcher, final long millis, boolean isVisible) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "Wait for a specific view to be " +
                        (isVisible ? "visible" : "invisible") + " during " + millis + " millis.";
            }

            @Override
            public void perform(final UiController uiController, final View view) {
                uiController.loopMainThreadUntilIdle();
                final long startTime = System.currentTimeMillis();
                final long endTime = startTime + millis;

                while (System.currentTimeMillis() < endTime) {
                    for (View child : TreeIterables.breadthFirstViewTraversal(view)) {
                        // found view with required ID
                        if (viewMatcher.matches(child) && (isVisible || !child.isShown())) {
                            return;
                        }
                    }

                    uiController.loopMainThreadForAtLeast(50);
                }

                // timeout happens
                throw new PerformException.Builder()
                        .withActionDescription(this.getDescription())
                        .withViewDescription(HumanReadables.describe(view))
                        .withCause(new TimeoutException())
                        .build();
            }
        };
    }

    /**
     * Input an answer for the article game.
     * @param context application Context.
     * @param language language selected in the game setup.
     * @param isCorrect specifies if the provided answer must be correct.
     * @return ViewAction that can allow to input an answer for the article game.
     */
    public static ViewAction inputArticleGameAnswer(Context context, Language language, boolean isCorrect) {
        return new ViewAction() {
            private TextView wordTextView;
            private EditText answerEditText;

            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "Inputs an answer to the Article Game";
            }

            @Override
            public void perform(final UiController uiController, final View view) {
                this.wordTextView = view.findViewById(R.id.word_text_view);
                this.answerEditText = view.findViewById(R.id.answer_input);

                if (isCorrect){
                    setCorrectAnswer();
                }else{
                    this.answerEditText.setText(TestTools.getRandomString(5));
                }
            }

            private void setCorrectAnswer(){
                WordDao wordDao = AppDatabase.getInstance(context).getWordDao();
                ArticleDao articleDao = AppDatabase.getInstance(context).getArticleDao();

                wordDao.getNounsByLanguageAsyncTask(language.getLanguageName(), words -> {
                    Word wordObjectInTextView = null;

                    for (Word word: words){
                        if (word.getWordName().equals(this.wordTextView.getText().toString())){
                            wordObjectInTextView = word;
                        }
                    }

                    if (wordObjectInTextView == null){
                        throw new NullPointerException("No word in the database matches the word shown.");
                    }

                    Word finalWordObjectInTextView = wordObjectInTextView;
                    articleDao.getAllRecordsTask(articles -> {
                        for (Article article: articles){
                            if (article.getId().equals(finalWordObjectInTextView.getIdArticle())){
                                answerEditText.setText(article.getArticleName());
                                return;
                            }
                        }

                        throw new NullPointerException("No article matches the word shown.");
                    }).execute();
                }).execute();
            }
        };
    }

    /**
     * Input an answer for the vocabulary game.
     * @param context application Context.
     * @param baseLanguage base language selected in the game setup.
     * @param isCorrect specifies if the provided answer must be correct.
     * @return ViewAction that can allow to input an answer for the vocabulary game.
     */
    public static ViewAction inputVocabularyGameAnswer(Context context, Language baseLanguage, boolean isCorrect) {
        return new ViewAction() {
            private TextView wordTextView;
            private EditText answerEditText;

            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "Inputs an answer to the Vocabulary Game";
            }

            @Override
            public void perform(final UiController uiController, final View view) {
                this.wordTextView = view.findViewById(R.id.word_text_view);
                this.answerEditText = view.findViewById(R.id.answer_input);

                if (isCorrect){
                    setCorrectAnswer();
                }else{
                    answerEditText.setText(TestTools.getRandomString(10));
                }
            }

            private void setCorrectAnswer(){
                WordDao wordDao = AppDatabase.getInstance(context).getWordDao();
                String wordShown = wordTextView.getText().toString();

                wordDao.getAllRecordsTask(result -> {
                    Word wordObjectShown = null;

                    for (Word word: result){
                        if (word.getWordName().equals(wordShown)){
                            wordObjectShown = word;
                            break;
                        }
                    }

                    if (wordObjectShown == null){
                        throw new NullPointerException("No word in language "+
                                baseLanguage.getLanguageName()+" matches the word "+wordShown+".");
                    }

                    Word finalWordObjectShown = wordObjectShown;
                    wordDao.getWordsByLanguageAsyncTask(baseLanguage.getLanguageName(), result1 -> {
                        List<Long> synonymsIds = finalWordObjectShown.getIdsSynonyms();

                        for (Word word: result1){
                            if (synonymsIds.contains(word.getId()) &&
                                    word.getLanguage().equals(baseLanguage.getLanguageName())){
                                answerEditText.setText(word.getWordName());
                                return;
                            }
                        }

                        throw new NullPointerException("No synonym in the base language "
                                + baseLanguage.getLanguageName() +" was found to the word shown "+wordShown+".");
                    }).execute();
                }).execute();
            }
        };
    }

    /**
     * Input an answer for the conjugation game.
     * @param context application Context.
     * @param language language selected in the game setup.
     * @param isCorrect specifies if the provided answer must be correct.
     * @return ViewAction that can allow to input an answer for the conjugation game.
     */
    public static ViewAction inputConjugationGameAnswer(Context context, Language language, boolean isCorrect) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "Inputs an answer to the Conjugation Game";
            }

            @Override
            public void perform(final UiController uiController, final View view) {
                TextView verbAndTenseTextView = view.findViewById(R.id.verb_and_tense_text_view);
                String verbAndTenseInTextView = verbAndTenseTextView.getText().toString();
                String verb = verbAndTenseInTextView.split(" - ")[0];
                String tense = verbAndTenseInTextView.split(" - ")[1];

                WordDao wordDao = AppDatabase.getInstance(context).getWordDao();
                wordDao.getWordsByCategoryAndByLanguageTask("verbs", language.getLanguageName(), verbsInLanguage -> {
                    ConjugationDao conjugationDao = AppDatabase.getInstance(context).getConjugationDao();
                    conjugationDao.getAllRecordsTask(conjugations -> {
                        Conjugation correctAnswer = TestTools.findConjugationOfVerb(verb, tense, verbsInLanguage, conjugations);

                        if (correctAnswer == null){
                            throw new NullPointerException("No valid answer was found in the specified verb and conjugation lists.");
                        }

                        EditText inputConjugation1 = view.findViewById(R.id.conjugation_1);
                        EditText inputConjugation2 = view.findViewById(R.id.conjugation_2);
                        EditText inputConjugation3 = view.findViewById(R.id.conjugation_3);
                        EditText inputConjugation4 = view.findViewById(R.id.conjugation_4);
                        EditText inputConjugation5 = view.findViewById(R.id.conjugation_5);
                        EditText inputConjugation6 = view.findViewById(R.id.conjugation_6);

                        inputConjugation1.setText(isCorrect?correctAnswer.getConjugation1(): TestTools.getRandomString(10));
                        inputConjugation2.setText(isCorrect?correctAnswer.getConjugation2(): TestTools.getRandomString(10));
                        inputConjugation3.setText(isCorrect?correctAnswer.getConjugation3(): TestTools.getRandomString(10));
                        inputConjugation4.setText(isCorrect?correctAnswer.getConjugation4(): TestTools.getRandomString(10));
                        inputConjugation5.setText(isCorrect?correctAnswer.getConjugation5(): TestTools.getRandomString(10));
                        inputConjugation6.setText(isCorrect?correctAnswer.getConjugation6(): TestTools.getRandomString(10));
                    }).execute();
                }).execute();
            }
        };
    }
}

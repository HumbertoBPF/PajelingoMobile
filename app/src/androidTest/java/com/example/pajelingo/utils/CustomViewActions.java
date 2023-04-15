package com.example.pajelingo.utils;

import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static com.example.pajelingo.utils.RandomTools.getRandomAlphabeticalString;
import static com.example.pajelingo.utils.TestTools.getDisplayedWord;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.pajelingo.ui.LabeledEditText;
import com.example.pajelingo.ui.LabeledSpinner;

import org.hamcrest.Matcher;

import java.util.List;
import java.util.concurrent.TimeoutException;

public class CustomViewActions {
    /**
     * Perform waits until the element is accessible and has the specified visibility.
     * @param viewMatcher A view matcher for the view to wait for.
     * @param millis The timeout of until when to wait for.
     * @param isVisible Boolean indicating the visibility that is awaited.
     */
    public static ViewAction waitUntil(Matcher<View> viewMatcher, final long millis, boolean isVisible) {
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
                return "Inputs an answer for the Article Game";
            }

            @Override
            public void perform(final UiController uiController, final View view) {
                this.wordTextView = view.findViewById(R.id.word_text_view);
                this.answerEditText = view.findViewById(R.id.answer_input);

                if (isCorrect){
                    setCorrectAnswer();
                }else{
                    typeTextAndCloseSoftKeyboard(uiController, this.answerEditText, getRandomAlphabeticalString(5));
                }
            }

            private void setCorrectAnswer() {
                WordDao wordDao = AppDatabase.getInstance(context).getWordDao();
                ArticleDao articleDao = AppDatabase.getInstance(context).getArticleDao();

                wordDao.getNounsByLanguageAsyncTask(language.getLanguageName(), words -> {
                    Word displayedWord = getDisplayedWord(this.wordTextView, words);

                    articleDao.getAllRecordsTask(articles -> {
                        Article article = getArticleOfWord(displayedWord, articles);
                        answerEditText.setText(article.getArticleName());
                    }).execute();
                }).execute();
            }

            private Article getArticleOfWord(Word displayedWord, List<Article> articles) {
                for (Article article : articles) {
                    if (article.getId().equals(displayedWord.getIdArticle())) {
                        return article;
                    }
                }

                throw new NullPointerException("No article matches the word shown "+displayedWord.getWordName());
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
    public static ViewAction inputVocabularyGameAnswer(Context context, Language baseLanguage, Language targetLanguage, boolean isCorrect) {
        return new ViewAction() {
            private TextView wordTextView;
            private EditText answerEditText;

            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "Inputs an answer for the Vocabulary Game";
            }

            @Override
            public void perform(final UiController uiController, final View view) {
                this.wordTextView = view.findViewById(R.id.word_text_view);
                this.answerEditText = view.findViewById(R.id.answer_input);

                if (isCorrect){
                    setCorrectAnswer();
                }else{
                    typeTextAndCloseSoftKeyboard(uiController, this.answerEditText, getRandomAlphabeticalString(10));
                }
            }

            private void setCorrectAnswer(){
                WordDao wordDao = AppDatabase.getInstance(context).getWordDao();

                wordDao.getWordsByLanguageAsyncTask(targetLanguage.getLanguageName(), targetLanguageWords -> {
                    Word displayedWord = getDisplayedWord(this.wordTextView, targetLanguageWords);

                    wordDao.getWordsByLanguageAsyncTask(baseLanguage.getLanguageName(), baseLanguageWords -> {
                        Word word = getSynonym(displayedWord, baseLanguageWords);
                        answerEditText.setText(word.getWordName());
                    }).execute();
                }).execute();
            }

            private Word getSynonym(Word displayedWord, List<Word> baseLanguageWords) {
                List<Long> synonymsIds = displayedWord.getIdsSynonyms();

                for (Word word: baseLanguageWords){
                    if (synonymsIds.contains(word.getId()) &&
                            word.getLanguage().equals(baseLanguage.getLanguageName())){
                        return word;
                    }
                }

                throw new NullPointerException("No synonym in the base language "
                        + baseLanguage.getLanguageName() +" was found to the word "+ displayedWord.getWordName() +".");
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

                        setAnswer(view, R.id.conjugation_1, correctAnswer.getConjugation1(), isCorrect);
                        setAnswer(view, R.id.conjugation_2, correctAnswer.getConjugation2(), isCorrect);
                        setAnswer(view, R.id.conjugation_3, correctAnswer.getConjugation3(), isCorrect);
                        setAnswer(view, R.id.conjugation_4, correctAnswer.getConjugation4(), isCorrect);
                        setAnswer(view, R.id.conjugation_5, correctAnswer.getConjugation5(), isCorrect);
                        setAnswer(view, R.id.conjugation_6, correctAnswer.getConjugation6(), isCorrect);
                    }).execute();
                }).execute();
            }

            private void setAnswer(View parentView, int inputId, String correctAnswer, boolean isCorrect) {
                LabeledEditText conjugation = parentView.findViewById(inputId);
                conjugation.getEditText().setText(isCorrect?correctAnswer: getRandomAlphabeticalString(10));
            }
        };
    }

    /**
     * Fills the EditText of a LabeledEditText with the specified string.
     * @param input text to be input in the LabeledEditText.
     * @return ViewAction object that performs the mentioned action.
     */
    public static ViewAction fillLabeledEditText(String input) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isDisplayed();
            }

            @Override
            public String getDescription() {
                return "Inputs a LabeledEditText with the text " + input;
            }

            @Override
            public void perform(final UiController uiController, final View view) {
                LabeledEditText labeledInput = (LabeledEditText) view;
                typeTextAndCloseSoftKeyboard(uiController, labeledInput.getEditText(), input);
            }
        };
    }

    public static ViewAction clickChildViewWithId(int id) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isDisplayed();
            }

            @Override
            public String getDescription() {
                return "Clicking on a child view with a specific id";
            }

            @Override
            public void perform(final UiController uiController, final View view) {
                ImageView iconHeart = view.findViewById(id);
                iconHeart.performClick();
            }
        };
    }

    /**
     * Opens the item list of a LabeledSpinner spinner.
     * @return ViewAction object that performs the referred action.
     */
    public static ViewAction expandSpinner() {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isDisplayed();
            }

            @Override
            public String getDescription() {
                return "Performs a click on the spinner of a LabeledSpinner";
            }

            @Override
            public void perform(final UiController uiController, final View view) {
                LabeledSpinner labeledSpinner = (LabeledSpinner) view;
                click().perform(uiController, labeledSpinner.getSpinner());
            }
        };
    }

    private static void typeTextAndCloseSoftKeyboard(UiController uiController, EditText editText, String text) {
        clearText().perform(uiController, editText);
        typeText(text).perform(uiController, editText);
        closeSoftKeyboard().perform(uiController, editText);
    }
}

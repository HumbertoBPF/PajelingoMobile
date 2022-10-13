package com.example.pajelingo.utils;

import static androidx.test.espresso.matcher.ViewMatchers.isRoot;

import static com.example.pajelingo.utils.Tools.getRandomString;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.test.espresso.PerformException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.util.HumanReadables;
import androidx.test.espresso.util.TreeIterables;

import com.example.pajelingo.R;
import com.example.pajelingo.models.Article;
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

    public static ViewAction inputArticleGameAnswer(List<Word> wordsInLanguage, List<Article> articles, boolean isCorrect) {
        return new ViewAction() {
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
                TextView wordTextView = view.findViewById(R.id.word_text_view);
                String wordInTextView = wordTextView.getText().toString();
                String answer = isCorrect?getCorrectArticle(wordInTextView):getRandomString(5);

                EditText answerEditText = view.findViewById(R.id.answer_input);
                answerEditText.setText(answer);
            }

            private String getCorrectArticle(String wordShown){
                Word wordObjectInTextView = null;
                Article wordArticle = null;

                for (Word word: wordsInLanguage){
                    if (word.getWordName().equals(wordShown)){
                        wordObjectInTextView = word;
                    }
                }

                if (wordObjectInTextView == null){
                    throw new NullPointerException("No word in the database matches the word shown.");
                }

                for (Article article: articles){
                    if (article.getId().equals(wordObjectInTextView.getIdArticle())){
                        wordArticle = article;
                    }
                }

                if (wordArticle == null){
                    throw new NullPointerException("No article matches the word shown.");
                }

                return wordArticle.getArticleName();
            }
        };
    }
}

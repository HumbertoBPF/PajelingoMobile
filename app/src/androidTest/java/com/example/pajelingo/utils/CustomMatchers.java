package com.example.pajelingo.utils;

import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import android.content.res.ColorStateList;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.matcher.BoundedMatcher;

import com.example.pajelingo.R;
import com.example.pajelingo.adapters.SearchResultsAdapter;
import com.example.pajelingo.models.Conjugation;
import com.example.pajelingo.models.Game;
import com.example.pajelingo.models.Meaning;
import com.example.pajelingo.models.Score;
import com.example.pajelingo.models.Word;
import com.example.pajelingo.ui.LabeledEditText;
import com.example.pajelingo.ui.LabeledView;
import com.example.pajelingo.ui.PasswordRequirement;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomMatchers {
    /**
     * Verifies if the item at the specified position of a RecyclerView contains the specified game name.
     * @param gameName game name that is expected to be at the specified position.
     * @param position position of the concerned item.
     * @return Matcher that performs the mentioned validation.
     */
    public static Matcher<? super View> isGameNameAtPosition(String gameName, int position){
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            private final Matcher<View> displayed = isDisplayed();
            @Override
            public void describeTo(Description description) {
                description.appendText("View with game ")
                        .appendValue(gameName)
                        .appendText(" at position ")
                        .appendValue(position)
                        .appendDescriptionOf(displayed);
            }

            @Override
            protected boolean matchesSafely(RecyclerView item) {
                RecyclerView.ViewHolder viewHolder = item.findViewHolderForAdapterPosition(position);

                if (viewHolder == null){
                    throw new IndexOutOfBoundsException("View at position "+position+" not found.");
                }

                View view = viewHolder.itemView;
                TextView gameNameTextView = view.findViewById(R.id.game_name_text_view);

                return gameNameTextView.getText().toString().equals(gameName) && displayed.matches(gameNameTextView);
            }
        };
    }

    /**
     * Verifies if the item at the specified position of a RecyclerView corresponds to the specified word.
     * @param word Word instance that is expected to be at the specified position.
     * @param position position of the concerned item.
     * @return Matcher that performs the mentioned validation.
     */
    public static Matcher<? super View> isWordAtPosition(Word word, int position, boolean hasFavorite){
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("Word ")
                        .appendValue(word.getWordName())
                        .appendText(" in ")
                        .appendValue(word.getLanguage())
                        .appendText(" is shown at position ")
                        .appendValue(position);
            }

            @Override
            protected boolean matchesSafely(RecyclerView item) {
                RecyclerView.ViewHolder viewHolder = item.findViewHolderForAdapterPosition(position);

                if (viewHolder == null){
                    throw new IndexOutOfBoundsException("View at position "+position+" not found.");
                }

                View itemView = viewHolder.itemView;

                TextView wordNameTextView = itemView.findViewById(R.id.word_name_text_view);
                ImageView iconHeart = itemView.findViewById(R.id.ic_heart);

                boolean textMatches = wordNameTextView.getText().toString().equals(word.getWordName());
                int iconHeartVisibility = iconHeart.getVisibility();

                if (hasFavorite) {
                    return (textMatches && (iconHeartVisibility == View.VISIBLE));
                }

                // TODO check if the flag image is properly rendered
                return (textMatches && (iconHeartVisibility == View.GONE));
            }
        };
    }

    /**
     * Verifies if the item at the specified position of a RecyclerView corresponds to the specified meaning.
     * @param meaning Meaning instance that is expected to be at the specified position.
     * @param position position of the concerned item.
     * @return Matcher that performs the mentioned validation.
     */
    public static Matcher<? super View> isMeaningAtPosition(Meaning meaning, int position){
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("Meaning with id ")
                        .appendValue(meaning.getId())
                        .appendText(" of word with id ")
                        .appendValue(meaning.getIdWord())
                        .appendText(" is shown at position ")
                        .appendValue(position);
            }

            @Override
            protected boolean matchesSafely(RecyclerView item) {
                RecyclerView.ViewHolder viewHolder = item.findViewHolderForAdapterPosition(position);

                if (viewHolder == null){
                    throw new IndexOutOfBoundsException("View at position "+position+" not found.");
                }

                View itemView = viewHolder.itemView;

                TextView meaningTextView = itemView.findViewById(R.id.meaning_text_view);

                return meaningTextView.getText().toString().equals(meaning.getMeaning());
            }
        };
    }

    /**
     * Asserts the content of a RecyclerView containing a list of search results:
     * <br>
     * <ul>
     *  <li>The results must be in alphabetic order</li>
     *  <li>The results must contain the search pattern (case insensitive search)</li>
     * </ul>
     * @param searchPattern search pattern to be matched.
     * @return Matcher that performs the mentioned validation.
     */
    public static Matcher<? super View> searchResultsMatchPattern(String searchPattern){
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("Search results match pattern ")
                        .appendValue(searchPattern)
                        .appendText(" and are in the alphabetic order");
            }

            @Override
            protected boolean matchesSafely(RecyclerView item) {
                String searchPatternLowercase = searchPattern.toLowerCase();

                SearchResultsAdapter adapter = (SearchResultsAdapter) item.getAdapter();

                if (adapter == null){
                    throw new NullPointerException("RecyclerView adapter not found.");
                }

                int nbItems = adapter.getItemCount();
                String lastWord = null;

                for (int i = 0;i < nbItems;i++){
                    String word = adapter.getItem(i).getWordName().toLowerCase();

                    // Verify if results contain the search pattern
                    if (!word.contains(searchPatternLowercase)) {
                        return false;
                    }
                    // Verify if the words are in the alphabetic order
                    if (lastWord != null){
                        List<String> orderedWords = new ArrayList<>();
                        orderedWords.add(lastWord);
                        orderedWords.add(word);
                        Collections.sort(orderedWords);
                        if (!orderedWords.get(0).equals(lastWord) || !orderedWords.get(1).equals(word)){
                            return false;
                        }
                    }

                    lastWord = word;
                }

                return true;
            }
        };
    }

    /**
     * Verifies if the word of a TextView is contained in the specified list.
     * @param words list of words concerned by the assertion.
     * @return Matcher that performs the mentioned validation.
     */
    public static Matcher<? super View> isTextViewWordInList(List<Word> words){
        return new BoundedMatcher<View, TextView>(TextView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("Word in TextView is in the specified list.");
            }

            @Override
            protected boolean matchesSafely(TextView item) {
                String wordInTextView = item.getText().toString();

                for (Word word: words){
                    if (wordInTextView.equals(word.getWordName())){
                        return true;
                    }
                }

                throw new NullPointerException("No word in the language matched the word displayed.");
            }
        };
    }

    /**
     * Verifies if the verb and the conjugation contained in a TextView are contained in the specified lists.
     * @param verbs list of verbs concerned by the assertion.
     * @param conjugations list of conjugations concerned by the assertion.
     * @return Matcher that performs the mentioned validation.
     */
    public static Matcher<? super View> isTextViewVerbAndConjugationInList(List<Word> verbs, List<Conjugation> conjugations){
        return new BoundedMatcher<View, TextView>(TextView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("Verb in TextView is in the specified list.");
            }

            @Override
            protected boolean matchesSafely(TextView item) {
                String verb = item.getText().toString().split(" - ")[0];
                String tense = item.getText().toString().split(" - ")[1];

                return TestTools.findConjugationOfVerb(verb, tense, verbs, conjugations) != null;
            }
        };
    }

    /**
     * Verifies if the provided feedback.
     * @param isCorrectAnswer specifies if the feedback is positive or negative.
     * @return Matcher that performs the mentioned validation.
     */
    public static Matcher<? super View> checkAnswerFeedback(boolean isCorrectAnswer){
        return new BoundedMatcher<View, TextView>(TextView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("Verifying if the feedback corresponds to a ")
                        .appendText(isCorrectAnswer?"correct":"incorrect")
                        .appendText(" answer.");
            }

            @Override
            protected boolean matchesSafely(TextView item) {
                String feedbackMessage = item.getText().toString();
                Log.i("FEEDBACK MESSAGE", feedbackMessage);
                String startText = isCorrectAnswer?"Correct :)":"Wrong answer";
                return feedbackMessage.startsWith(startText);
            }
        };
    }

    /**
     * Verifies if the item at the specified position of a ranking recycler view contains the
     * specified score.
     * @param score score that is expected to be at the specified position.
     * @param position position of the concerned item.
     * @return Matcher that performs the mentioned validation.
     */
    public static Matcher<? super View> isScoreAtPositionInRanking(Score score, int position){
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("Verifying if the item at the position ")
                        .appendValue(position)
                        .appendText(" of the ranking corresponds to the specified score.");
            }

            @Override
            protected boolean matchesSafely(RecyclerView item) {
                RecyclerView.ViewHolder viewHolder = item.findViewHolderForAdapterPosition(position);
                if (viewHolder == null){
                    throw new IndexOutOfBoundsException("View at position "+position+" not found.");
                }

                View view = viewHolder.itemView;
                TextView positionTextView = view.findViewById(R.id.position_text_view);
                TextView usernameTextView = view.findViewById(R.id.username_text_view);
                TextView scoreTextView = view.findViewById(R.id.score_text_view);

                return positionTextView.getText().toString().equals(String.valueOf(position+1)) &&
                        usernameTextView.getText().toString().equals(score.getUser()) &&
                        scoreTextView.getText().toString().equals(String.valueOf(score.getScore()));
            }
        };
    }

    /**
     * Verifies if the item at the specified position of an account score list recycler view
     * contains the specified score.
     * @param score score that is expected to be at the specified position.
     * @param game game referred by the specified score.
     * @param position position of the concerned item.
     * @return Matcher that performs the mentioned validation.
     */
    public static Matcher<? super View> isScoreAtPositionInProfile(Score score, Game game, int position){
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("Verifying if the item at position ")
                        .appendValue(position)
                        .appendText(" of the profile score list corresponds to the specified score.");
            }

            @Override
            protected boolean matchesSafely(RecyclerView item) {
                RecyclerView.ViewHolder viewHolder = item.findViewHolderForAdapterPosition(position);

                if (viewHolder == null){
                    throw new IndexOutOfBoundsException("View at position "+position+" not found.");
                }

                View itemView = viewHolder.itemView;

                TextView gameTextView = itemView.findViewById(R.id.game_text_view);
                TextView scoreTextView = itemView.findViewById(R.id.score_text_view);

                if (!gameTextView.getText().toString().equals(game.getGameName())){
                    throw new AssertionError("The game names do not match.\n" +
                            "Expected: "+game.getGameName()+"\n" +
                            "Actual: "+gameTextView.getText().toString());
                }

                if (!scoreTextView.getText().toString().equals(score.getScore().toString())){
                    throw new AssertionError("The score values do not match.\n" +
                            "Expected: "+score.getScore()+"\n" +
                            "Actual: "+scoreTextView.getText().toString());
                }

                return true;
            }
        };
    }

    /**
     * Checks if the background color of a Floating Action Button matches the specified color.
     * @param color color resource.
     * @return Matcher that performs the mentioned validation.
     */
    public static Matcher<? super View> floatActionButtonHasColor(int color){
        return new BoundedMatcher<View, FloatingActionButton>(FloatingActionButton.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("Verifying if the color of the FloatingActionButton matches the specified color");
            }

            @Override
            protected boolean matchesSafely(FloatingActionButton floatingActionButton) {
                ColorStateList backgroundTintList = floatingActionButton.getBackgroundTintList();

                if (backgroundTintList == null){
                    throw new NullPointerException("No ColorStateList could be obtained from the FloatingActionButton");
                }

                return floatingActionButton.getBackgroundTintList().equals(ColorStateList.valueOf(color));
            }
        };
    }

    /**
     * Checks if the text in the label of a LabeledView matches the specified string.
     * @param label string to be compared to the label text.
     * @return Matcher that performs the mentioned validation.
     */
    public static Matcher<? super View> hasLabel(String label){
        return new BoundedMatcher<View, LabeledView>(LabeledView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("Verifying if the label of the LabeledView is "+label);
            }

            @Override
            protected boolean matchesSafely(LabeledView labeledView) {
                return labeledView.getLabel().toString().equals(label);
            }
        };
    }

    public static Matcher<? super View> hasInput(String text){
        return new BoundedMatcher<View, LabeledEditText>(LabeledEditText.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("Verifying if the label of the LabeledEditText is "+text);
            }

            @Override
            protected boolean matchesSafely(LabeledEditText labeledView) {
                return labeledView.getEditText().getText().toString().equals(text);
            }
        };
    }

    public static Matcher<? super View> hasRequirementText(String text){
        return new BoundedMatcher<View, PasswordRequirement>(PasswordRequirement.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("Verifying if the text of the PasswordRequirement view is "+text);
            }

            @Override
            protected boolean matchesSafely(PasswordRequirement passwordRequirement) {
                return passwordRequirement.getText().toString().equals(text);
            }
        };
    }

    public static Matcher<? super View> isChecked(boolean isChecked){
        return new BoundedMatcher<View, PasswordRequirement>(PasswordRequirement.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("Verifying if the text of the PasswordRequirement view is checked");
            }

            @Override
            protected boolean matchesSafely(PasswordRequirement passwordRequirement) {
                return passwordRequirement.isChecked() == isChecked;
            }
        };
    }
}

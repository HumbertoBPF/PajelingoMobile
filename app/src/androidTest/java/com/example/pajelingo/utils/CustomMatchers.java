package com.example.pajelingo.utils;

import android.content.res.ColorStateList;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.matcher.BoundedMatcher;

import com.example.pajelingo.models.Conjugation;
import com.example.pajelingo.models.Word;
import com.example.pajelingo.ui.LabeledEditText;
import com.example.pajelingo.ui.LabeledView;
import com.example.pajelingo.ui.PasswordRequirement;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.List;

public class CustomMatchers {
    public static Matcher<? super View> atPosition(Matcher<View> matcher, int position) {
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                matcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(RecyclerView item) {
                RecyclerView.ViewHolder viewHolder = item.findViewHolderForAdapterPosition(position);

                if (viewHolder == null) {
                    throw new IndexOutOfBoundsException("A ViewHolder matching the specified index could not be found");
                }

                return matcher.matches(viewHolder.itemView);
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

    /**
     * Checks if the number of items of a RecyclerView matches the expected number of items.
     * @param nbOfItems expected number of items
     * @return Matcher that performs the specified assertion over the number of items of the RecyclerView
     */
    public static Matcher<? super View> hasLength(int nbOfItems){
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("Verifying if the number of items of a RecyclerView is ").appendValue(nbOfItems);
            }

            @Override
            protected boolean matchesSafely(RecyclerView item) {
                if (item.getAdapter() == null) {
                    throw new NullPointerException("No adapter was found for the provided RecyclerView");
                }
                return item.getAdapter().getItemCount() == nbOfItems;
            }
        };
    }
}

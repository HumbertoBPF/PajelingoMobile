package com.example.pajelingo.utils;

import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.matcher.BoundedMatcher;

import com.example.pajelingo.R;
import com.example.pajelingo.adapters.SearchResultsAdapter;
import com.example.pajelingo.models.Meaning;
import com.example.pajelingo.models.Word;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomMatchers {
    public static Matcher<? super View> isGameNameAtPosition(int position, String gameName){
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            private Matcher<View> displayed = isDisplayed();
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

    public static Matcher<? super View> isScoreAtPosition(Word word, int position){
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
                TextView languageTextView = itemView.findViewById(R.id.language_text_view);

                return wordNameTextView.getText().toString().equals(word.getWordName()) &&
                        languageTextView.getText().toString().equals(word.getLanguage());
            }
        };
    }

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

    public static Matcher<? super View> searchResultsMatchPattern(String searchPattern){
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("Search results match pattern ").appendValue(searchPattern);
            }

            @Override
            protected boolean matchesSafely(RecyclerView item) {
                SearchResultsAdapter adapter = (SearchResultsAdapter) item.getAdapter();

                if (adapter == null){
                    throw new NullPointerException("RecyclerView adapter not found.");
                }

                int nbItems = adapter.getItemCount();
                String lastWord = null;

                for (int i = 0;i < nbItems;i++){
                    String word = adapter.getItem(i).getWordName().toLowerCase();

                    // Verify if results contain the search pattern
                    if (!word.contains(searchPattern)) {
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
}

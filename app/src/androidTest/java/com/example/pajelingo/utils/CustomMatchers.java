package com.example.pajelingo.utils;

import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.matcher.BoundedMatcher;

import com.example.pajelingo.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class CustomMatchers {
    public static Matcher<? super View> showGameItemInPosition(int position, String gameName){
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
}

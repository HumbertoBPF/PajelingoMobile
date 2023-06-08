package com.example.pajelingo.retrofit_calls;

import static com.example.pajelingo.utils.Notifications.showBadgeNotification;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.pajelingo.R;
import com.example.pajelingo.models.Badge;
import com.example.pajelingo.models.GameAnswerFeedback;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameScoreCall extends IdlingResource{
    private final Context context;

    public GameScoreCall(Context context) {
        this.context = context;
    }

    public void execute(Call<GameAnswerFeedback> call) {
        incrementIdlingResource();
        call.enqueue(new Callback<GameAnswerFeedback>() {
            @Override
            public void onResponse(@NonNull Call<GameAnswerFeedback> call, @NonNull Response<GameAnswerFeedback> response) {
                GameAnswerFeedback gameAnswerFeedback = response.body();

                if ((response.isSuccessful()) && (gameAnswerFeedback != null)) {
                    String currentGameScore = context.getString(R.string.current_game_score, gameAnswerFeedback.getScore());
                    Toast.makeText(context, currentGameScore, Toast.LENGTH_SHORT).show();

                    List<Badge> newBadges = gameAnswerFeedback.getNewBadges();

                    if (newBadges != null) {
                        if (newBadges.size() == 1) {
                            showBadgeNotification(context, newBadges.get(0));
                        } else if (newBadges.size() > 1) {
                            showBadgeNotification(context, newBadges);
                        }
                    }
                }else {
                    Toast.makeText(context, R.string.error_game_score, Toast.LENGTH_SHORT).show();
                }

                decrementIdlingResource();
            }

            @Override
            public void onFailure(@NonNull Call<GameAnswerFeedback> call, @NonNull Throwable t) {
                Toast.makeText(context, R.string.error_game_score, Toast.LENGTH_SHORT).show();
                decrementIdlingResource();
            }
        });
    }
}

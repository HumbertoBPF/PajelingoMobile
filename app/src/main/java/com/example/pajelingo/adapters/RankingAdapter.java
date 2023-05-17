package com.example.pajelingo.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pajelingo.R;
import com.example.pajelingo.activities.account.ProfileActivity;
import com.example.pajelingo.models.Score;
import com.example.pajelingo.models.User;

import java.util.List;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.RankingViewHolder> {
    private final Context context;
    private final List<Score> scores;

    public RankingAdapter(Context context, List<Score> scores) {
        this.context = context;
        this.scores = scores;
    }

    @NonNull
    @Override
    public RankingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ranking_item_layout, parent, false);
        return new RankingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingViewHolder holder, int position) {
        holder.bind(scores.get(position), position);
    }

    @Override
    public int getItemCount() {
        return scores.size();
    }

    class RankingViewHolder extends RecyclerView.ViewHolder{

        private final TextView positionTextView;
        private final TextView usernameTextView;
        private final TextView scoreTextView;

        public RankingViewHolder(@NonNull View itemView) {
            super(itemView);
            this.positionTextView = itemView.findViewById(R.id.position_text_view);
            this.usernameTextView = itemView.findViewById(R.id.username_text_view);
            this.scoreTextView = itemView.findViewById(R.id.score_text_view);
        }

        public void bind(Score score, int position){
            this.positionTextView.setText(String.valueOf(position+1));
            this.usernameTextView.setText(score.getUser());
            this.scoreTextView.setText(String.valueOf(score.getScore()));

            itemView.setOnClickListener(view -> {
                User user = new User(null, score.getUser(), null, null);
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("account", user);
                context.startActivity(intent);
            });
        }

    }
}

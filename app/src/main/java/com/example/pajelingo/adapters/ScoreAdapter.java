package com.example.pajelingo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pajelingo.R;
import com.example.pajelingo.models.Score;

import java.util.List;

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder> {
    private final List<Score> scores;

    public ScoreAdapter(List<Score> scores) {
        this.scores = scores;
    }

    @NonNull
    @Override
    public ScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.score_item_layout, parent, false);
        return new ScoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreViewHolder holder, int position) {
        holder.bind(scores.get(position));
    }

    @Override
    public int getItemCount() {
        return scores.size();
    }

    class ScoreViewHolder extends RecyclerView.ViewHolder{
        private final TextView gameTextView;
        private final TextView scoreTextView;

        public ScoreViewHolder(@NonNull View itemView) {
            super(itemView);
            gameTextView = itemView.findViewById(R.id.game_text_view);
            scoreTextView = itemView.findViewById(R.id.score_text_view);
        }

        public void bind(Score score){
            gameTextView.setText(score.getGame());
            scoreTextView.setText(score.getScore().toString());
        }
    }
}

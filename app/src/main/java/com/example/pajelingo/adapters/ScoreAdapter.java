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

    private List<Score> scores;

    public ScoreAdapter(List<Score> scores) {
        this.scores = scores;
    }

    @NonNull
    @Override
    public ScoreAdapter.ScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.score_item_layout, parent, false);
        return new ScoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreAdapter.ScoreViewHolder holder, int position) {
        holder.bind(scores.get(position), position);
    }

    @Override
    public int getItemCount() {
        return scores.size();
    }

    class ScoreViewHolder extends RecyclerView.ViewHolder{

        private TextView positionTextView;
        private TextView usernameTextView;
        private TextView scoreTextView;

        public ScoreViewHolder(@NonNull View itemView) {
            super(itemView);
            this.positionTextView = itemView.findViewById(R.id.position_text_view);
            this.usernameTextView = itemView.findViewById(R.id.username_text_view);
            this.scoreTextView = itemView.findViewById(R.id.score_text_view);
        }

        public void bind(Score score, int position){
            this.positionTextView.setText((position+1)+"");
            this.usernameTextView.setText(score.getUser());
            this.scoreTextView.setText(score.getScore().toString());
        }

    }
}

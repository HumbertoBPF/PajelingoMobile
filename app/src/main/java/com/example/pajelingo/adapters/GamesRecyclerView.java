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

import java.util.List;

public class GamesRecyclerView extends RecyclerView.Adapter<GamesRecyclerView.GameViewHolder> {

    private Context context;
    private List<String> games;

    public GamesRecyclerView(Context context, List<String> games){
        this.context = context;
        this.games = games;
    }

    @NonNull
    @Override
    public GamesRecyclerView.GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_item_layout, parent, false);
        return new GameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GamesRecyclerView.GameViewHolder holder, int position) {
        holder.bind(context, games.get(position));
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    class GameViewHolder extends RecyclerView.ViewHolder{
        private final TextView gameNameTextView;

        public GameViewHolder(@NonNull View itemView) {
            super(itemView);
            this.gameNameTextView = itemView.findViewById(R.id.game_name_text_view);
        }

        public void bind(Context context, String gameName){
            this.gameNameTextView.setText(gameName);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Class destinyClass = Class.forName("com.example.pajelingo.activities.games."+gameName.replace(" ","")+"Activity");
                        context.startActivity(new Intent(context, destinyClass));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }

}

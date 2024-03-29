package com.example.pajelingo.adapters;

import static com.example.pajelingo.utils.Files.setImageResourceFromFile;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pajelingo.R;
import com.example.pajelingo.models.Game;

import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder> {

    private final Context context;
    private final List<Game> games;

    public GameAdapter(Context context, List<Game> games){
        this.context = context;
        this.games = games;
    }

    @NonNull
    @Override
    public GameAdapter.GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_item_layout, parent, false);
        return new GameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameAdapter.GameViewHolder holder, int position) {
        holder.bind(context, games.get(position));
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    class GameViewHolder extends RecyclerView.ViewHolder{
        private final ImageView gameImageView;
        private final TextView gameNameTextView;

        public GameViewHolder(@NonNull View itemView) {
            super(itemView);
            this.gameImageView = itemView.findViewById(R.id.game_image_view);
            this.gameNameTextView = itemView.findViewById(R.id.game_name_text_view);
        }

        public void bind(Context context, Game game){
            String path = context.getFilesDir()+game.getImageUri();
            setImageResourceFromFile(gameImageView, path);
            this.gameNameTextView.setText(game.getGameName());
            itemView.setOnClickListener(v -> {
                try {
                    Class<?> destinyClass = Class.forName("com.example.pajelingo.activities.games."
                            + game.getAndroidGameActivity());
                    Intent intent = new Intent(context, destinyClass);
                    intent.putExtra("game", game);
                    context.startActivity(intent);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });
        }

    }

}

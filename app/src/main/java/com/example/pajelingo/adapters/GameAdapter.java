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
import com.example.pajelingo.models.Game;

import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder> {

    private Context context;
    private List<Game> games;

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
        private final TextView gameNameTextView;

        public GameViewHolder(@NonNull View itemView) {
            super(itemView);
            this.gameNameTextView = itemView.findViewById(R.id.game_name_text_view);
        }

        public void bind(Context context, Game game){
            this.gameNameTextView.setText(game.getGameName());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String gameActivityName = getGameActivityClassName(game);

                        Class destinyClass = Class.forName("com.example.pajelingo.activities.games."
                                +  gameActivityName + "Activity");
                        Intent intent = new Intent(context, destinyClass);
                        intent.putExtra("game", game);
                        context.startActivity(intent);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        // TODO test this function
        private String getGameActivityClassName(Game game) {
            String gameActivityName = "";
            Character currentChar;
            Character lastChar = ' ';
            String gameName = game.getGameName();

            for (int i = 0; i < gameName.length(); i++){
                currentChar = gameName.charAt(i);

                if (!currentChar.equals(' ')){
                    if (lastChar.equals(' ')){
                        currentChar = Character.toUpperCase(currentChar);
                    }
                    gameActivityName += currentChar;
                }

                lastChar = currentChar;
            }

            return gameActivityName;
        }

    }

}

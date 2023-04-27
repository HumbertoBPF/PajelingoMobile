package com.example.pajelingo.synchronization;

import static com.example.pajelingo.utils.Files.saveImage;

import android.content.Context;

import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Game;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;

import java.io.IOException;
import java.util.List;

public class GameSynchro extends ResourcesSynchro<Game>{
    private final Context context;

    public GameSynchro(Context context) {
        super(AppDatabase.getInstance(context).getGameDao(),
                () -> LanguageSchoolAPIHelper.getApiObject().getGames(), new ScoresSynchro(context));
        this.context = context;
    }

    @Override
    protected void saveEntities(List<Game> entities) {
        super.saveEntities(entities);

        for (Game game: entities) {
            try {
                saveGameImage(game);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void saveGameImage(Game game) throws IOException {
        String base64String = game.getImage();
        String path = game.getImageUri();
        if ((base64String != null) && (path != null)) {
            saveImage(context, base64String, path);
        }
    }
}

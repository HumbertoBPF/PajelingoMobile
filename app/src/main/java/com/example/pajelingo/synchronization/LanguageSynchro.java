package com.example.pajelingo.synchronization;

import static com.example.pajelingo.utils.Tools.saveImage;

import android.content.Context;

import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Language;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;

import java.io.IOException;
import java.util.List;

public class LanguageSynchro extends ResourcesSynchro<Language> {
    private final Context context;

    public LanguageSynchro(Context context) {
        super(AppDatabase.getInstance(context).getLanguageDao(),
                () -> LanguageSchoolAPIHelper.getApiObject().getLanguages(), new MeaningSynchro(context));
        this.context = context;
    }

    @Override
    protected void saveEntities(List<Language> entities) {
        super.saveEntities(entities);

        for (Language language: entities){
            try {
                saveFlagImage(language);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveFlagImage(Language language) throws IOException {
        String base64String = language.getFlagImage();
        String path = language.getFlagImageUri();
        if ((base64String != null) && (path != null)){
            saveImage(context, base64String, path);
        }
    }
}

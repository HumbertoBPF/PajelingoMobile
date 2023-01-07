package com.example.pajelingo.synchronization;

import static com.example.pajelingo.utils.Tools.getPictureFromBase64String;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.appcompat.app.AlertDialog;

import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Language;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class LanguageSynchro extends ResourcesSynchro<Language> {
    private final Context context;

    public LanguageSynchro(Context context, AlertDialog downloadDialog) {
        super("language", AppDatabase.getInstance(context).getLanguageDao(),
                () -> LanguageSchoolAPIHelper.getApiObject().getLanguages(), new MeaningSynchro(context, downloadDialog), downloadDialog);
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
        if ((language.getFlagImage() != null) && (language.getFlagImageUri() != null)){

            String uri = language.getFlagImageUri();
            String[] subPaths = uri.split("/");

            int nbPaths = subPaths.length;
            File parentFile = context.getFilesDir();

            for (int i = 0;i < nbPaths-1;i++){
                String subDirectory = subPaths[i];
                File file = new File(parentFile, subDirectory);
                file.mkdir();
                parentFile = file;
            }

            String filename = subPaths[nbPaths - 1];

            File imageFile = new File(parentFile,filename);

            FileOutputStream fos = new FileOutputStream(imageFile);
            // Use the compress method on the BitMap object to write image to the OutputStream
            getPictureFromBase64String(language.getFlagImage()).compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        }
    }
}

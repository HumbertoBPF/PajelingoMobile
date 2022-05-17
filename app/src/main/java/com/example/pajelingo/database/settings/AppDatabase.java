package com.example.pajelingo.database.settings;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.pajelingo.daos.CategoryDao;
import com.example.pajelingo.daos.ConjugationDao;
import com.example.pajelingo.daos.LanguageDao;
import com.example.pajelingo.daos.MeaningDao;
import com.example.pajelingo.daos.WordDao;
import com.example.pajelingo.database.converters.SynonymsConverter;
import com.example.pajelingo.models.Category;
import com.example.pajelingo.models.Conjugation;
import com.example.pajelingo.models.Language;
import com.example.pajelingo.models.Meaning;
import com.example.pajelingo.models.Word;

@Database(entities = {Category.class, Conjugation.class, Language.class, Meaning.class, Word.class}, version = 1)
@TypeConverters(SynonymsConverter.class)
public abstract class AppDatabase extends RoomDatabase {
    private static final String NAME_DB = "Pajelingo.db";

    public abstract CategoryDao getCategoryDao();
    public abstract ConjugationDao getConjugationDao();
    public abstract LanguageDao getLanguageDao();
    public abstract MeaningDao getMeaningDao();
    public abstract WordDao getWordDao();

    public static AppDatabase getInstance(Context context){
        return Room.databaseBuilder(context,AppDatabase.class,NAME_DB).build();
    }
}

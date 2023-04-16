package com.example.pajelingo.activities.search_tool;

import static com.example.pajelingo.utils.Tools.displayFavoriteWordError;
import static com.example.pajelingo.utils.Tools.getAuthToken;
import static com.example.pajelingo.utils.Tools.getPictureFromBase64String;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pajelingo.R;
import com.example.pajelingo.adapters.MeaningAdapter;
import com.example.pajelingo.daos.MeaningDao;
import com.example.pajelingo.daos.WordDao;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.FavoriteWordPayload;
import com.example.pajelingo.models.Image;
import com.example.pajelingo.models.Word;
import com.example.pajelingo.retrofit.LanguageSchoolAPI;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeaningActivity extends AppCompatActivity {

    private Word word;

    private ImageView meaningImageView;
    private RecyclerView meaningsRecyclerView;
    private MaterialButton favoriteWordButton;

    private LanguageSchoolAPI languageSchoolAPI;

    private WordDao wordDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meaning);

        meaningImageView = findViewById(R.id.meaning_image);
        meaningsRecyclerView = findViewById(R.id.meanings_recycler_view);
        favoriteWordButton = findViewById(R.id.favorite_word_button);

        languageSchoolAPI = LanguageSchoolAPIHelper.getApiObject();

        wordDao = AppDatabase.getInstance(this).getWordDao();

        word = (Word) getIntent().getSerializableExtra("word");

        setTitle(word.getWordName());

        MeaningDao meaningDao = AppDatabase.getInstance(MeaningActivity.this).getMeaningDao();
        meaningDao.getMeaningOfWord(word.getId(),
                result -> meaningsRecyclerView.setAdapter(new MeaningAdapter(result)));

        setWordImage();
        setFavoriteWordButtonLayout();

        favoriteWordButton.setOnClickListener(v -> {
            toggleFavoriteWord();
        });
    }

    private void setFavoriteWordButtonLayout() {
        if (word.getFavorite() == null) {
            favoriteWordButton.setVisibility(View.GONE);
        }else{
            favoriteWordButton.setVisibility(View.VISIBLE);

            if (word.getFavorite()) {
                favoriteWordButton.setText(R.string.remove_from_favorite_words);
                favoriteWordButton.setIconResource(R.drawable.ic_favorite_border);
            }else{
                favoriteWordButton.setText(R.string.add_to_favorite_words);
                favoriteWordButton.setIconResource(R.drawable.ic_favorite);
            }
        }
    }

    private void toggleFavoriteWord(){
        Boolean isFavorite = word.getFavorite();
        FavoriteWordPayload payload =  new FavoriteWordPayload(!isFavorite);
        Call<Word> call = languageSchoolAPI.favoriteWord(getAuthToken(MeaningActivity.this), payload, word.getId());

        call.enqueue(new Callback<Word>() {
            @Override
            public void onResponse(Call<Word> call, Response<Word> response) {
                Word returnedWord = response.body();

                if ((response.isSuccessful()) && (returnedWord != null)) {
                    List<Word> wordList = new ArrayList<>();
                    wordList.add(returnedWord);
                    wordDao.save(wordList, result -> {
                        word = returnedWord;
                        setFavoriteWordButtonLayout();
                    });
                }else {
                    displayFavoriteWordError(MeaningActivity.this, word);
                }
            }

            @Override
            public void onFailure(Call<Word> call, Throwable t) {
                Toast.makeText(MeaningActivity.this, R.string.warning_connection_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setWordImage() {
        if (word.getImage() != null){
            Call<Image> call = LanguageSchoolAPIHelper.getApiObject().getPublicImage(word.getImage());
            call.enqueue(new Callback<Image>() {
                @Override
                public void onResponse(Call<Image> call, Response<Image> response) {
                    if (response.isSuccessful()){
                        Image image = response.body();
                        if (image != null){
                            meaningImageView.setImageBitmap(getPictureFromBase64String(image.getImage()));
                        }
                    }
                }

                @Override
                public void onFailure(Call<Image> call, Throwable t) {

                }
            });
        }
    }
}
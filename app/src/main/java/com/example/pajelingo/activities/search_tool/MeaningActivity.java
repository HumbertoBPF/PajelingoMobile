package com.example.pajelingo.activities.search_tool;

import static com.example.pajelingo.utils.Files.getPictureFromBase64String;
import static com.example.pajelingo.utils.Tools.displayFavoriteWordError;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pajelingo.R;
import com.example.pajelingo.adapters.MeaningAdapter;
import com.example.pajelingo.daos.MeaningDao;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.interfaces.HttpResponseInterface;
import com.example.pajelingo.models.Image;
import com.example.pajelingo.models.Word;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;
import com.example.pajelingo.retrofit_calls.FavoriteWordCall;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeaningActivity extends AppCompatActivity {

    private Word word;

    private ImageView meaningImageView;
    private RecyclerView meaningsRecyclerView;
    private MaterialButton favoriteWordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meaning);

        meaningImageView = findViewById(R.id.meaning_image);
        meaningsRecyclerView = findViewById(R.id.meanings_recycler_view);
        favoriteWordButton = findViewById(R.id.favorite_word_button);

        word = (Word) getIntent().getSerializableExtra("word");

        setTitle(word.getWordName());

        MeaningDao meaningDao = AppDatabase.getInstance(MeaningActivity.this).getMeaningDao();
        meaningDao.getMeaningOfWord(word.getId(),
                result -> meaningsRecyclerView.setAdapter(new MeaningAdapter(result)));

        setWordImage();
        setFavoriteWordButtonLayout();

        favoriteWordButton.setOnClickListener(v -> toggleFavoriteWord());
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
        FavoriteWordCall favoriteWordCall = new FavoriteWordCall(this);

        favoriteWordCall.execute(word, new HttpResponseInterface<Word>() {
            @Override
            public void onSuccess(Word returnedWord) {
                word = returnedWord;
                setFavoriteWordButtonLayout();
            }

            @Override
            public void onError(Response<Word> response) {
                displayFavoriteWordError(MeaningActivity.this, word);
            }

            @Override
            public void onFailure() {
                Toast.makeText(MeaningActivity.this, R.string.warning_connection_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setWordImage() {
        if (word.getImage() != null){
            Call<Image> call = LanguageSchoolAPIHelper.getApiObject().getPublicImage(word.getImage());
            call.enqueue(new Callback<Image>() {
                @Override
                public void onResponse(@NonNull Call<Image> call, @NonNull Response<Image> response) {
                    if (response.isSuccessful()){
                        Image image = response.body();
                        if (image != null){
                            meaningImageView.setImageBitmap(getPictureFromBase64String(image.getImage()));
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Image> call, @NonNull Throwable t) {

                }
            });
        }
    }
}
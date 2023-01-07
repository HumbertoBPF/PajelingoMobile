package com.example.pajelingo.activities.search_tool;

import static com.example.pajelingo.utils.Tools.getPictureFromBase64String;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pajelingo.R;
import com.example.pajelingo.adapters.MeaningAdapter;
import com.example.pajelingo.daos.MeaningDao;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Image;
import com.example.pajelingo.models.Word;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeaningActivity extends AppCompatActivity {

    private ImageView meaningImageView;
    private RecyclerView meaningsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meaning);

        meaningImageView = findViewById(R.id.meaning_image);
        meaningsRecyclerView = findViewById(R.id.meanings_recycler_view);

        Word word = (Word) getIntent().getSerializableExtra("word");

        setTitle(word.getWordName());

        MeaningDao meaningDao = AppDatabase.getInstance(MeaningActivity.this).getMeaningDao();
        meaningDao.getMeaningOfWordTask(word.getId(),
                result -> meaningsRecyclerView.setAdapter(new MeaningAdapter(result))).execute();

        setWordImage(word);
    }

    private void setWordImage(Word word) {
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
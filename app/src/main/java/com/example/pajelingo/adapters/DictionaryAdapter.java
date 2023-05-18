package com.example.pajelingo.adapters;

import static com.example.pajelingo.utils.Files.setImageResourceFromFile;
import static com.example.pajelingo.utils.SharedPreferences.isUserAuthenticated;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.Guideline;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pajelingo.R;
import com.example.pajelingo.activities.dictionary.MeaningActivity;
import com.example.pajelingo.daos.LanguageDao;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.interfaces.OnFavoriteItem;
import com.example.pajelingo.models.Word;

import java.util.List;

public class DictionaryAdapter extends RecyclerView.Adapter<DictionaryAdapter.SearchResultsViewHolder> {

    private final Context context;
    private final List<Word> words;
    private final LanguageDao languageDao;
    private final OnFavoriteItem onFavoriteItem;

    public DictionaryAdapter(Context context, List<Word> words, OnFavoriteItem onFavoriteItem){
        this.context = context;
        this.words = words;
        this.languageDao = AppDatabase.getInstance(context).getLanguageDao();
        this.onFavoriteItem = onFavoriteItem;
    }

    @NonNull
    @Override
    public DictionaryAdapter.SearchResultsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_results_item_layout, parent, false);
        return new DictionaryAdapter.SearchResultsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DictionaryAdapter.SearchResultsViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    public void setWordAtPosition(Word word, int position) {
        this.words.set(position, word);
        notifyItemChanged(position);
    }

    public class SearchResultsViewHolder extends RecyclerView.ViewHolder{

        private final TextView wordNameTextView;
        private final ImageView languageImageView;
        private final CardView rootView;
        private final Guideline guideline85;
        private final ImageView heartIcon;

        public SearchResultsViewHolder(@NonNull View itemView) {
            super(itemView);
            wordNameTextView = itemView.findViewById(R.id.word_name_text_view);
            languageImageView = itemView.findViewById(R.id.language_image_view);
            rootView = itemView.findViewById(R.id.root_view);
            guideline85 = itemView.findViewById(R.id.guideline_85);
            heartIcon = itemView.findViewById(R.id.ic_heart);
        }
        
        private void setHeartIcon(boolean isFavorite) {
            if (isFavorite) {
                heartIcon.setImageResource(R.drawable.ic_favorite);
            }else {
                heartIcon.setImageResource(R.drawable.ic_favorite_border);
            }
        }

        public void bind(int position){
            Word word = words.get(position);

            wordNameTextView.setText(word.getWordName());

            if ((word.getFavorite() == null) || (!isUserAuthenticated(context))){
                heartIcon.setVisibility(View.GONE);
                heartIcon.setOnClickListener(null);
                guideline85.setGuidelinePercent(1.0f);
            }else {
                heartIcon.setVisibility(View.VISIBLE);
                guideline85.setGuidelinePercent(0.75f);
                setHeartIcon(word.getFavorite());

                heartIcon.setOnClickListener(v -> onFavoriteItem.favoriteItem(word, position));
            }

            rootView.setOnClickListener(v -> {
                Intent intent = new Intent(context, MeaningActivity.class);
                intent.putExtra("word", word);
                context.startActivity(intent);
            });
            // Setting the language flag from internal memory
            languageDao.getLanguageByName(word.getLanguage(), result -> {
                String path = context.getFilesDir()+result.getFlagImageUri();
                setImageResourceFromFile(languageImageView, path);
            });
        }
    }
}

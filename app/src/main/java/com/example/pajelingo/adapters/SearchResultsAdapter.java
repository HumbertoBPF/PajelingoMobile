package com.example.pajelingo.adapters;

import static com.example.pajelingo.utils.Tools.isUserAuthenticated;
import static com.example.pajelingo.utils.Tools.setImageResourceFromFile;

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
import com.example.pajelingo.activities.search_tool.MeaningActivity;
import com.example.pajelingo.daos.LanguageDao;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.interfaces.OnFavoriteItem;
import com.example.pajelingo.models.Word;

import java.util.List;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.SearchResultsViewHolder> {

    private final Context context;
    private final List<Word> words;
    private final LanguageDao languageDao;
    private final OnFavoriteItem onFavoriteItem;

    public SearchResultsAdapter(Context context, List<Word> words, OnFavoriteItem onFavoriteItem){
        this.context = context;
        this.words = words;
        this.languageDao = AppDatabase.getInstance(context).getLanguageDao();
        this.onFavoriteItem = onFavoriteItem;
    }

    @NonNull
    @Override
    public SearchResultsAdapter.SearchResultsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_results_item_layout, parent, false);
        return new SearchResultsAdapter.SearchResultsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultsAdapter.SearchResultsViewHolder holder, int position) {
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
        private final Guideline guideline75;
        private final ImageView heartIcon;

        public SearchResultsViewHolder(@NonNull View itemView) {
            super(itemView);
            wordNameTextView = itemView.findViewById(R.id.word_name_text_view);
            languageImageView = itemView.findViewById(R.id.language_image_view);
            rootView = itemView.findViewById(R.id.root_view);
            guideline75 = itemView.findViewById(R.id.guideline_75);
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
                guideline75.setGuidelinePercent(1.0f);
            }else {
                heartIcon.setVisibility(View.VISIBLE);
                guideline75.setGuidelinePercent(0.75f);
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

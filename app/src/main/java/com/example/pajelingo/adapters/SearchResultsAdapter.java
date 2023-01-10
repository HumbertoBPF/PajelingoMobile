package com.example.pajelingo.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pajelingo.R;
import com.example.pajelingo.activities.search_tool.MeaningActivity;
import com.example.pajelingo.daos.LanguageDao;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Word;

import java.io.File;
import java.util.List;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.SearchResultsViewHolder> {

    private final Context context;
    private final List<Word> words;
    private final LanguageDao languageDao;

    public SearchResultsAdapter(Context context, List<Word> words){
        this.context = context;
        this.words = words;
        this.languageDao = AppDatabase.getInstance(context).getLanguageDao();
    }

    @NonNull
    @Override
    public SearchResultsAdapter.SearchResultsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_results_item_layout, parent, false);
        return new SearchResultsAdapter.SearchResultsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultsAdapter.SearchResultsViewHolder holder, int position) {
        holder.bind(words.get(position));
    }

    public Word getItem(int position){
        return words.get(position);
    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    public class SearchResultsViewHolder extends RecyclerView.ViewHolder{

        private final TextView wordNameTextView;
        private final ImageView languageImageView;
        private final CardView rootView;

        public SearchResultsViewHolder(@NonNull View itemView) {
            super(itemView);
            wordNameTextView = itemView.findViewById(R.id.word_name_text_view);
            languageImageView = itemView.findViewById(R.id.language_image_view);
            rootView = itemView.findViewById(R.id.root_view);
        }

        public void bind(Word word){
            wordNameTextView.setText(word.getWordName());
            rootView.setOnClickListener(v -> {
                Intent intent = new Intent(context, MeaningActivity.class);
                intent.putExtra("word", word);
                context.startActivity(intent);
            });
            // Setting the language flag from internal memory
            languageDao.getLanguageByNameAsyncTask(word.getLanguage(), result -> {
                String path = context.getFilesDir()+result.getFlagImageUri();
                File languageImageFile = new File(path);
                if(languageImageFile.exists()){
                    Bitmap image = BitmapFactory.decodeFile(languageImageFile.getAbsolutePath());
                    languageImageView.setImageBitmap(image);
                }else{
                    languageImageView.setImageBitmap(null);
                }
            }).execute();
        }
    }
}

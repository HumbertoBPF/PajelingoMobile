package com.example.pajelingo.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pajelingo.R;
import com.example.pajelingo.activities.MeaningActivity;
import com.example.pajelingo.daos.LanguageDao;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Word;

import java.util.List;

public class SearchResultsRecyclerView extends RecyclerView.Adapter<SearchResultsRecyclerView.SearchResultsViewHolder> {

    private final Context context;
    private final List<Word> words;

    public SearchResultsRecyclerView(Context context, List<Word> words){
        this.context = context;
        this.words = words;
    }

    @NonNull
    @Override
    public SearchResultsRecyclerView.SearchResultsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_results_item_layout, parent, false);
        return new SearchResultsRecyclerView.SearchResultsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultsRecyclerView.SearchResultsViewHolder holder, int position) {
        holder.bind(words.get(position));
    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    public class SearchResultsViewHolder extends RecyclerView.ViewHolder{

        private final TextView wordNameTextView;
        private final TextView languageTextView;
        private final ConstraintLayout rootView;

        public SearchResultsViewHolder(@NonNull View itemView) {
            super(itemView);
            wordNameTextView = itemView.findViewById(R.id.word_name_text_view);
            languageTextView = itemView.findViewById(R.id.language_text_view);
            rootView = itemView.findViewById(R.id.root_view);
        }

        public void bind(Word word){
            LanguageDao languageDao = AppDatabase.getInstance(context).getLanguageDao();
            wordNameTextView.setText(word.getWordName());
            languageDao.findRecordByIdTask(word.getIdLanguage(), result -> languageTextView.setText(result.getLanguageName())).execute();
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MeaningActivity.class);
                    intent.putExtra("wordId", word.getId());
                    context.startActivity(intent);
                }
            });
        }
    }
}

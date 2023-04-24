package com.example.pajelingo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pajelingo.R;
import com.example.pajelingo.models.Meaning;

import java.util.List;

public class MeaningAdapter extends RecyclerView.Adapter<MeaningAdapter.MeaningViewHolder> {

    private final List<Meaning> meanings;

    public MeaningAdapter(List<Meaning> meanings) {
        this.meanings = meanings;
    }

    @NonNull
    @Override
    public MeaningViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meaning_item_layout, parent, false);
        return new MeaningViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MeaningViewHolder holder, int position) {
        holder.bind(meanings.get(position));
    }

    @Override
    public int getItemCount() {
        return meanings.size();
    }

    class MeaningViewHolder extends RecyclerView.ViewHolder{

        private final TextView meaningTextView;

        public MeaningViewHolder(@NonNull View itemView) {
            super(itemView);
            this.meaningTextView = itemView.findViewById(R.id.meaning_text_view);
        }

        public void bind(Meaning meaning){
            this.meaningTextView.setText(meaning.getMeaning());
        }

    }

}

package com.example.pajelingo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pajelingo.R;
import com.example.pajelingo.models.MenuItem;

import java.util.List;

public class MenuItemAdapter extends RecyclerView.Adapter<MenuItemAdapter.MenuItemViewHolder> {
    private final List<MenuItem> menuItems;

    public MenuItemAdapter(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    @NonNull
    @Override
    public MenuItemAdapter.MenuItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item_layout, parent, false);
        return new MenuItemAdapter.MenuItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuItemAdapter.MenuItemViewHolder holder, int position) {
        holder.bind(menuItems.get(position));
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    class MenuItemViewHolder extends RecyclerView.ViewHolder{
        private final ImageView iconImageView;
        private final TextView titleTextView;
        private final TextView descriptionTextView;

        public MenuItemViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImageView = itemView.findViewById(R.id.icon_image_view);
            titleTextView = itemView.findViewById(R.id.title_text_view);
            descriptionTextView = itemView.findViewById(R.id.description_text_view);
        }

        public void bind(MenuItem menuItem){
            Integer iconResource = menuItem.getIconResource();
            String title = menuItem.getTitle();
            String description = menuItem.getDescription();

            if (menuItem.getIconResource() != null){
                iconImageView.setVisibility(View.VISIBLE);
                iconImageView.setImageResource(iconResource);
            }else{
                iconImageView.setVisibility(View.GONE);
            }

            if (menuItem.getTitle() != null){
                titleTextView.setVisibility(View.VISIBLE);
                titleTextView.setText(title);
            }else{
                titleTextView.setVisibility(View.GONE);
            }

            if (menuItem.getDescription() != null){
                descriptionTextView.setVisibility(View.VISIBLE);
                descriptionTextView.setText(description);
            }else{
                descriptionTextView.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(menuItem.getOnClickListener());
        }
    }
}

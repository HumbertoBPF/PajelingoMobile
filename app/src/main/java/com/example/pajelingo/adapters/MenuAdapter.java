package com.example.pajelingo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pajelingo.R;
import com.example.pajelingo.models.MenuItem;
import com.example.pajelingo.ui.SimpleListItem;

import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuItemViewHolder> {
    private final List<MenuItem> menuItems;

    public MenuAdapter(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    @NonNull
    @Override
    public MenuItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item_layout, parent, false);
        return new MenuAdapter.MenuItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuItemViewHolder holder, int position) {
        holder.bind(menuItems.get(position));
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    class MenuItemViewHolder extends RecyclerView.ViewHolder{
        private final SimpleListItem menuItem;

        public MenuItemViewHolder(@NonNull View itemView) {
            super(itemView);
            this.menuItem = itemView.findViewById(R.id.menu_item);
        }

        public void bind(MenuItem menuItem) {
            this.menuItem.setTitle(menuItem.getTitle());
            this.menuItem.setDescription(menuItem.getDescription());

            if (menuItem.getIcon() != null) {
                this.menuItem.setIcon(menuItem.getIcon());
            }

            this.menuItem.setOnClickListener(menuItem.getOnClickListener());
        }
    }
}

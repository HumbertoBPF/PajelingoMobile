package com.example.pajelingo.adapters;

import static com.example.pajelingo.utils.Files.getPictureFromBase64String;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pajelingo.R;
import com.example.pajelingo.models.Badge;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class BadgesAdapter extends RecyclerView.Adapter<BadgesAdapter.BadgesViewHolder> {
    private final Context context;
    private final List<Badge> badges;

    public BadgesAdapter(Context context, List<Badge> badges) {
        this.context = context;
        this.badges = badges;
    }

    @NonNull
    @Override
    public BadgesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.badges_item_layout, parent, false);
        return new BadgesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BadgesViewHolder holder, int position) {
        holder.bind(badges.get(position));
    }

    @Override
    public int getItemCount() {
        return badges.size();
    }

    class BadgesViewHolder extends RecyclerView.ViewHolder {
        private final MaterialButton badgeMaterialButton;

        public BadgesViewHolder(@NonNull View itemView) {
            super(itemView);
            badgeMaterialButton = itemView.findViewById(R.id.badge_material_button);
        }

        public void bind(Badge badge) {
            BitmapDrawable drawable =
                    new BitmapDrawable(context.getResources(), getPictureFromBase64String(badge.getImage()));
            badgeMaterialButton.setIcon(drawable);
            badgeMaterialButton.setText(badge.getName());
            badgeMaterialButton.setBackgroundColor(Color.parseColor("#" + badge.getColor()));

            badgeMaterialButton.setOnClickListener(view -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(badge.getName())
                        .setMessage(badge.getDescription())
                        .setPositiveButton(R.string.badge_details_confirm_button_text, (dialog, id) -> dialog.dismiss());
                AlertDialog confirmationDialog = builder.create();
                confirmationDialog.setCancelable(false);
                confirmationDialog.show();
            });
        }
    }
}

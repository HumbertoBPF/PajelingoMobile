package com.example.pajelingo.adapters;

import static com.example.pajelingo.utils.Files.getPictureFromBase64String;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pajelingo.R;
import com.example.pajelingo.activities.account.ProfileActivity;
import com.example.pajelingo.models.User;

import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountViewHolder> {
    private final Context context;
    private final List<User> accounts;

    public AccountAdapter(Context context, List<User> accounts) {
        this.context = context;
        this.accounts = accounts;
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.account_item_layout, parent, false);
        return new AccountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {
        holder.bind(accounts.get(position));
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }

    class AccountViewHolder extends RecyclerView.ViewHolder {
        private final ImageView accountImageView;
        private final TextView accountTextView;

        public AccountViewHolder(@NonNull View itemView) {
            super(itemView);
            accountImageView = itemView.findViewById(R.id.account_image_view);
            accountTextView = itemView.findViewById(R.id.account_text_view);
        }

        public void bind(User account) {
            String base64Picture = account.getPicture();

            if (base64Picture != null) {
                accountImageView.setImageBitmap(getPictureFromBase64String(base64Picture));
            } else {
                accountImageView.setImageResource(R.drawable.profile);
            }

            accountTextView.setText(account.getUsername());

            itemView.setOnClickListener(view -> {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("account", account);
                context.startActivity(intent);
            });
        }
    }
}

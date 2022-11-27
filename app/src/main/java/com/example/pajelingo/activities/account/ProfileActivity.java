package com.example.pajelingo.activities.account;

import static com.example.pajelingo.utils.Tools.isUserAuthenticated;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pajelingo.R;
import com.google.android.material.button.MaterialButton;

public class ProfileActivity extends AppCompatActivity {
    private TextView usernameCredentialTextView;
    private TextView emailCredentialTextView;
    private MaterialButton editAccountButton;
    private MaterialButton deleteAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setTitle(getString(R.string.profile_activity_title));

        usernameCredentialTextView = findViewById(R.id.username_credential_text_view);
        emailCredentialTextView = findViewById(R.id.email_credential_text_view);
        editAccountButton = findViewById(R.id.edit_account_button);
        deleteAccountButton = findViewById(R.id.delete_account_button);

        SharedPreferences sp = getSharedPreferences(getString(R.string.sp_file_name), MODE_PRIVATE);

        usernameCredentialTextView.setText(getString(R.string.username_label)+": "+sp.getString(getString(R.string.username_sp),  ""));
        emailCredentialTextView.setText(getString(R.string.email_label)+": "+sp.getString(getString(R.string.email_sp), ""));

        editAccountButton.setOnClickListener(v -> {

        });

        deleteAccountButton.setOnClickListener(v -> askConfirmationDeletion());
    }

    private void askConfirmationDeletion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_delete_account_title)
                .setMessage(R.string.dialog_delete_account_message)
                .setPositiveButton(R.string.dialog_delete_account_confirm, (dialog, id) -> {
                    startActivity(new Intent(ProfileActivity.this, DeletionConfirmationActivity.class));
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.dialog_delete_account_decline, (dialog, which) -> dialog.dismiss());
        AlertDialog confirmationDialog = builder.create();
        confirmationDialog.setCancelable(false);
        confirmationDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Verifies if the user credentials were deleted while he was out from this activity
        if (!isUserAuthenticated(this)){
            finish();
        }
    }
}
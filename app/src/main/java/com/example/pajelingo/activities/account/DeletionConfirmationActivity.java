package com.example.pajelingo.activities.account;

import static com.example.pajelingo.utils.SharedPreferences.deleteUserData;
import static com.example.pajelingo.utils.SharedPreferences.getAuthToken;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pajelingo.R;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;
import com.example.pajelingo.ui.LoadingButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeletionConfirmationActivity extends AppCompatActivity {
    private EditText confirmDeletionEditText;
    private LoadingButton confirmDeletionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deletion_confirmation);

        setTitle(getString(R.string.confirm_deletion_activity_title));

        confirmDeletionEditText = findViewById(R.id.confirm_deletion_edit_text);
        confirmDeletionButton = findViewById(R.id.confirm_deletion_button);

        confirmDeletionButton.setOnClickListener(v -> requestAccountDeletion());
    }

    private void requestAccountDeletion() {
        String confirmationText = confirmDeletionEditText.getText().toString();
        confirmDeletionButton.setLoading(true);

        if (confirmationText.equals(getString(R.string.confirm_deletion_string))){
            Call<Void> call = LanguageSchoolAPIHelper.getApiObject().deleteAccount(getAuthToken(DeletionConfirmationActivity.this));
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    confirmDeletionButton.setLoading(false);
                    if (response.isSuccessful()){
                        Toast.makeText(DeletionConfirmationActivity.this, R.string.account_deletion_success, Toast.LENGTH_SHORT).show();
                        deleteUserData(DeletionConfirmationActivity.this);
                        finish();
                    }else{
                        Toast.makeText(DeletionConfirmationActivity.this, R.string.warning_request_error, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    confirmDeletionButton.setLoading(false);
                    Toast.makeText(DeletionConfirmationActivity.this, R.string.warning_request_error, Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(DeletionConfirmationActivity.this, R.string.warning_confirm_deletion, Toast.LENGTH_SHORT).show();
        }
    }
}
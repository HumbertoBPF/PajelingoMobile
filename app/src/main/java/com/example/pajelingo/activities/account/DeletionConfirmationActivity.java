package com.example.pajelingo.activities.account;

import static com.example.pajelingo.utils.SharedPreferences.deleteUserData;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pajelingo.R;
import com.example.pajelingo.interfaces.HttpResponseInterface;
import com.example.pajelingo.retrofit_calls.AccountDeletionCall;
import com.example.pajelingo.ui.LoadingButton;

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
            AccountDeletionCall accountDeletionCall = new AccountDeletionCall(DeletionConfirmationActivity.this);

            accountDeletionCall.execute(new HttpResponseInterface<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    confirmDeletionButton.setLoading(false);
                    Toast.makeText(DeletionConfirmationActivity.this, R.string.account_deletion_success, Toast.LENGTH_SHORT).show();
                    deleteUserData(DeletionConfirmationActivity.this);
                    finish();
                }

                @Override
                public void onError(Response<Void> response) {
                    confirmDeletionButton.setLoading(false);
                    Toast.makeText(DeletionConfirmationActivity.this, R.string.warning_request_error, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure() {
                    confirmDeletionButton.setLoading(false);
                    Toast.makeText(DeletionConfirmationActivity.this, R.string.warning_request_error, Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(DeletionConfirmationActivity.this, R.string.warning_confirm_deletion, Toast.LENGTH_SHORT).show();
        }
    }
}
package com.example.pajelingo.activities.account;

import static android.util.Patterns.EMAIL_ADDRESS;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pajelingo.R;
import com.example.pajelingo.models.ResetEmail;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestResetPasswordActivity extends AppCompatActivity {

    private EditText emailEditText;
    private Button resetPasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_reset_password);

        emailEditText = findViewById(R.id.email_edit_text);
        resetPasswordButton = findViewById(R.id.reset_password_button);

        resetPasswordButton.setOnClickListener(v -> {
            CharSequence email = emailEditText.getText();

            if (email.toString().trim().isEmpty()){
                Toast.makeText(RequestResetPasswordActivity.this, R.string.warning_email_required, Toast.LENGTH_SHORT).show();
            }else if (!EMAIL_ADDRESS.matcher(email).matches()){
                Toast.makeText(RequestResetPasswordActivity.this, R.string.warning_email_format_required, Toast.LENGTH_SHORT).show();
            }else{
                requestResetAccount(email);
            }
        });
    }

    private void requestResetAccount(CharSequence email) {
        AlertDialog loadingDialog = new AlertDialog.Builder(RequestResetPasswordActivity.this).setTitle(R.string.reset_password_loading_dialog_title)
                .setMessage(R.string.reset_password_loading_dialog_message)
                .setCancelable(false).create();
        AlertDialog feedbackDialog = new AlertDialog.Builder(RequestResetPasswordActivity.this).setTitle(R.string.reset_password_feedback_dialog_title)
                .setMessage(R.string.reset_password_feedback_dialog_message)
                .setPositiveButton(R.string.reset_password_positive_button_text, (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                })
                .setCancelable(false).create();

        loadingDialog.show();

        new Handler().postDelayed(() -> {
            Call<Void> call = LanguageSchoolAPIHelper.getApiObject().resetAccount(new ResetEmail(email.toString()));
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    loadingDialog.dismiss();
                    if (!response.isSuccessful()){
                        feedbackDialog.setTitle(R.string.reset_password_error_dialog_title);
                        feedbackDialog.setMessage(getString(R.string.warning_connection_error));
                    }
                    feedbackDialog.show();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    loadingDialog.dismiss();
                    feedbackDialog.setTitle(R.string.reset_password_error_dialog_title);
                    feedbackDialog.setMessage(getString(R.string.warning_connection_error));
                    feedbackDialog.show();
                }
            });
        }, 2000);
    }
}
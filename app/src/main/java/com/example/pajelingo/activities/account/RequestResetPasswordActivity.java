package com.example.pajelingo.activities.account;

import static android.util.Patterns.EMAIL_ADDRESS;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pajelingo.R;
import com.example.pajelingo.interfaces.HttpResponseInterface;
import com.example.pajelingo.retrofit_calls.RequestResetPasswordCall;
import com.example.pajelingo.ui.LoadingButton;

import retrofit2.Response;

public class RequestResetPasswordActivity extends AppCompatActivity {

    private EditText emailEditText;
    private LoadingButton resetPasswordButton;

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
        resetPasswordButton.setLoading(true);

        AlertDialog feedbackDialog = new AlertDialog.Builder(RequestResetPasswordActivity.this).setTitle(R.string.reset_password_feedback_dialog_title)
                .setMessage(R.string.reset_password_feedback_dialog_message)
                .setPositiveButton(R.string.reset_password_positive_button_text, (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                })
                .setCancelable(false).create();

        RequestResetPasswordCall requestResetPasswordCall = new RequestResetPasswordCall();

        requestResetPasswordCall.execute(email.toString(), new HttpResponseInterface<Void>() {
            @Override
            public void onSuccess(Void v) {
                onResponse(feedbackDialog);
            }

            @Override
            public void onError(Response<Void> response) {
                onErrorOrFailure(feedbackDialog);
            }

            @Override
            public void onFailure() {
                onErrorOrFailure(feedbackDialog);
            }
        });
    }

    private void onResponse(AlertDialog feedbackDialog) {
        resetPasswordButton.setLoading(false);
        feedbackDialog.show();
    }

    private void onErrorOrFailure(AlertDialog feedbackDialog) {
        feedbackDialog.setTitle(R.string.reset_password_error_dialog_title);
        feedbackDialog.setMessage(getString(R.string.warning_connection_error));
        onResponse(feedbackDialog);
    }
}
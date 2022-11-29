package com.example.pajelingo.activities.account;

import static com.example.pajelingo.utils.Tools.getAuthToken;
import static com.example.pajelingo.utils.Tools.saveStateAndUserCredentials;
import static com.example.pajelingo.utils.Tools.validatePassword;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pajelingo.R;
import com.example.pajelingo.models.User;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;
import com.example.pajelingo.ui.LabeledEditText;
import com.example.pajelingo.ui.PasswordRequirement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormUserActivity extends AppCompatActivity {

    private TextView loginLinkTextView;
    private LabeledEditText emailInput;
    private LabeledEditText usernameInput;
    private LabeledEditText passwordInput;
    private LabeledEditText passwordConfirmationInput;
    private Button submitButton;
    private PasswordRequirement passwordRequirement1;
    private PasswordRequirement passwordRequirement2;
    private PasswordRequirement passwordRequirement3;
    private PasswordRequirement passwordRequirement4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        loginLinkTextView = findViewById(R.id.login_link_text_view);
        emailInput = findViewById(R.id.email_input);
        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        passwordConfirmationInput = findViewById(R.id.password_confirmation_input);
        submitButton = findViewById(R.id.submit_button);
        passwordRequirement1 = findViewById(R.id.requirement_1);
        passwordRequirement2 = findViewById(R.id.requirement_2);
        passwordRequirement3 = findViewById(R.id.requirement_3);
        passwordRequirement4 = findViewById(R.id.requirement_4);

        addPasswordListener();

        Intent intent = getIntent();
        User authenticatedUser = (User) intent.getSerializableExtra("authenticatedUser");

        if (authenticatedUser != null){
            setTitle(getString(R.string.update_account_activity_title));

            emailInput.getEditText().setText(authenticatedUser.getEmail());
            usernameInput.getEditText().setText(authenticatedUser.getUsername());

            loginLinkTextView.setVisibility(View.GONE);

            submitButton.setText(R.string.update_account_button_text);
            submitButton.setOnClickListener(v -> submit(false));
        }else{
            setTitle(getString(R.string.signup_activity_title));

            loginLinkTextView.setOnClickListener(v -> {
                startActivity(new Intent(FormUserActivity.this, LoginActivity.class));
                finish();
            });

            submitButton.setOnClickListener(v -> submit(true));
        }
    }

    private void submit(boolean isSignup) {
        String email = emailInput.getEditText().getText().toString();
        String username = usernameInput.getEditText().getText().toString();
        String password = passwordInput.getEditText().getText().toString();

        if (!isInputValid()) {
            return;
        }

        if (isSignup){
            signup(email, username, password);
        }else{
            update(email, username, password);
        }
    }

    private void signup(String email, String username, String password) {
        Call<User> call = LanguageSchoolAPIHelper.getApiObject().signup(new User(email, username, password));
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.code() == 201) {
                    Toast.makeText(FormUserActivity.this, R.string.successful_signup_message, Toast.LENGTH_SHORT).show();
                    finish();
                }else if (response.code() == 400) {
                    String error = getValidationErrorMessage(response);
                    Toast.makeText(FormUserActivity.this, error, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(FormUserActivity.this, R.string.warning_connection_error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(FormUserActivity.this, R.string.warning_connection_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void update(String email, String username, String password) {
        SharedPreferences sp = getSharedPreferences(getString(R.string.sp_file_name), MODE_PRIVATE);

        String currentUsername = sp.getString(getString(R.string.username_sp), "");
        String currentPassword = sp.getString(getString(R.string.password_sp), "");

        Call<User> call = LanguageSchoolAPIHelper.getApiObject().updateAccount(getAuthToken(currentUsername, currentPassword),
                new User(email, username, password));
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.code() == 200) {
                    Toast.makeText(FormUserActivity.this, R.string.successful_update_message, Toast.LENGTH_SHORT).show();
                    saveStateAndUserCredentials(FormUserActivity.this, email, username, password);
                    finish();
                }else if (response.code() == 400) {
                    String error = getValidationErrorMessage(response);
                    Toast.makeText(FormUserActivity.this, error, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(FormUserActivity.this, R.string.warning_connection_error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(FormUserActivity.this, R.string.warning_connection_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addPasswordListener() {
        passwordInput.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                HashMap<String, Boolean> passwordMap = validatePassword(s.toString());

                passwordRequirement1.setChecked(Boolean.TRUE.equals(passwordMap.get("hasDigit")));
                passwordRequirement2.setChecked(Boolean.TRUE.equals(passwordMap.get("hasLetter")));
                passwordRequirement3.setChecked(Boolean.TRUE.equals(passwordMap.get("hasSpecialCharacter")));
                passwordRequirement4.setChecked(Boolean.TRUE.equals(passwordMap.get("hasValidLength")));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private boolean isInputValid() {
        String password = passwordInput.getEditText().getText().toString();
        String passwordConfirmation = passwordConfirmationInput.getEditText().getText().toString();

        if (!(passwordRequirement1.isChecked() &&
                passwordRequirement2.isChecked() &&
                passwordRequirement3.isChecked() &&
                passwordRequirement4.isChecked())){
            Toast.makeText(FormUserActivity.this, R.string.warning_password_requirements, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.equals(passwordConfirmation)){
            Toast.makeText(FormUserActivity.this, R.string.warning_password_confirmation, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private String getValidationErrorMessage(Response<User> response) {
        String error = getString(R.string.warning_form_validation);

        if (response.errorBody() != null){
            try {
                String errorBodyString = response.errorBody().string();
                JSONObject jsonObject = new JSONObject(errorBodyString.trim());
                JSONArray errorsArray = null;
                String fieldError = null;
                if (jsonObject.has("username")){
                    errorsArray = jsonObject.getJSONArray("username");
                    fieldError = getString(R.string.username_label) + ": ";
                }else if (jsonObject.has("email")){
                    errorsArray = jsonObject.getJSONArray("email");
                    fieldError = getString(R.string.email_label) + ": ";
                }else if (jsonObject.has("password")){
                    errorsArray = jsonObject.getJSONArray("password");
                    fieldError = getString(R.string.password_label) + ": ";
                }

                if (errorsArray != null){
                    error = fieldError + errorsArray.getString(0);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }

        return error;
    }
}
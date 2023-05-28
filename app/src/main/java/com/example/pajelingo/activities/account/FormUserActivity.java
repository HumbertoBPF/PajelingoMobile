package com.example.pajelingo.activities.account;

import static com.example.pajelingo.utils.SharedPreferences.saveUserData;
import static com.example.pajelingo.utils.Tools.validatePassword;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pajelingo.R;
import com.example.pajelingo.interfaces.HttpResponseInterface;
import com.example.pajelingo.models.User;
import com.example.pajelingo.retrofit_calls.SignupCall;
import com.example.pajelingo.retrofit_calls.UpdateProfileCall;
import com.example.pajelingo.ui.LabeledEditText;
import com.example.pajelingo.ui.LoadingButton;
import com.example.pajelingo.ui.PasswordRequirement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import retrofit2.Response;

public class FormUserActivity extends AppCompatActivity {

    private TextView loginLinkTextView;
    private LabeledEditText emailInput;
    private LabeledEditText usernameInput;
    private LabeledEditText bioInput;
    private LabeledEditText passwordInput;
    private LabeledEditText passwordConfirmationInput;
    private LoadingButton submitButton;
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
        bioInput = findViewById(R.id.bio_input);
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
            bioInput.getEditText().setText(authenticatedUser.getBio());

            loginLinkTextView.setVisibility(View.GONE);

            submitButton.setText(getString(R.string.update_account_button_text));
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
        submitButton.setLoading(true);

        String email = emailInput.getEditText().getText().toString();
        String username = usernameInput.getEditText().getText().toString();
        String bio = bioInput.getEditText().getText().toString();
        String password = passwordInput.getEditText().getText().toString();

        if (!isInputValid()) {
            submitButton.setLoading(false);
            return;
        }

        User user = new User(email, username, password, null, bio);

        if (isSignup){
            submitSignup(user);
        }else{
            submitUpdate(user);
        }
    }

    private void submitSignup(User user) {
        SignupCall call = new SignupCall();

        call.execute(user, new HttpResponseInterface<User>() {
            @Override
            public void onSuccess(User user) {
                submitButton.setLoading(false);
                Toast.makeText(FormUserActivity.this, R.string.successful_signup_message, Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(Response<User> response) {
                FormUserActivity.this.onError(response);
            }

            @Override
            public void onFailure() {

                FormUserActivity.this.onFailure();
            }
        });
    }

    private void submitUpdate(User user) {
        UpdateProfileCall call = new UpdateProfileCall(this);

        call.execute(user, new HttpResponseInterface<User>() {
            @Override
            public void onSuccess(User user) {
                submitButton.setLoading(false);
                Toast.makeText(FormUserActivity.this, R.string.successful_update_message, Toast.LENGTH_SHORT).show();
                user.setPassword(user.getPassword());
                saveUserData(FormUserActivity.this, user);
                finish();
            }

            @Override
            public void onError(Response<User> response) {
                FormUserActivity.this.onError(response);
            }

            @Override
            public void onFailure() {
                FormUserActivity.this.onFailure();
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

    private void onError(Response<User> response) {
        submitButton.setLoading(false);
        String error = getValidationErrorMessage(response);
        Toast.makeText(FormUserActivity.this, error, Toast.LENGTH_SHORT).show();
    }

    private void onFailure() {
        submitButton.setLoading(false);
        Toast.makeText(FormUserActivity.this, R.string.warning_connection_error, Toast.LENGTH_SHORT).show();
    }
}
package com.example.pajelingo.activities.account;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pajelingo.R;
import com.example.pajelingo.adapters.AccountAdapter;
import com.example.pajelingo.models.Page;
import com.example.pajelingo.models.User;
import com.example.pajelingo.retrofit.LanguageSchoolAPI;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchAccountActivity extends AppCompatActivity {
    private EditText searchAccountEditText;
    private Button searchAccountButton;
    private RecyclerView accountsRecyclerView;

    private LanguageSchoolAPI languageSchoolAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_account);

        setTitle(R.string.search_account_menu_item_title);

        searchAccountEditText = findViewById(R.id.search_account_edit_text);
        searchAccountButton = findViewById(R.id.search_account_button);
        accountsRecyclerView = findViewById(R.id.accounts_recycler_view);

        languageSchoolAPI = LanguageSchoolAPIHelper.getApiObject();

        searchAccountButton.setOnClickListener(view -> {
            String q = searchAccountEditText.getText().toString();

            Call<Page<User>> accountsCall = languageSchoolAPI.getAccounts(q);
            accountsCall.enqueue(new Callback<Page<User>>() {
                @Override
                public void onResponse(@NonNull Call<Page<User>> call, @NonNull Response<Page<User>> response) {
                    Page<User> page = response.body();

                    if ((response.isSuccessful()) && (page != null)) {
                        accountsRecyclerView.setAdapter(new AccountAdapter(SearchAccountActivity.this, page.getResults()));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Page<User>> call, @NonNull Throwable t) {

                }
            });
        });
    }
}
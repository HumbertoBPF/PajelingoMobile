package com.example.pajelingo.activities.account;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pajelingo.R;
import com.example.pajelingo.adapters.AccountAdapter;
import com.example.pajelingo.models.Page;
import com.example.pajelingo.models.User;
import com.example.pajelingo.retrofit.LanguageSchoolAPI;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;
import com.example.pajelingo.ui.LoadingButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchAccountActivity extends AppCompatActivity {
    private EditText searchAccountEditText;
    private LoadingButton searchAccountButton;
    private RecyclerView accountsRecyclerView;
    private ProgressBar loadingIcon;

    private LanguageSchoolAPI languageSchoolAPI;
    private String q;
    private int pageIndex = 0;
    private Page<User> page = null;

    private AccountAdapter adapter;

    private final Handler handler = new Handler();
    private boolean isLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_account);

        setTitle(R.string.search_account_menu_item_title);

        searchAccountEditText = findViewById(R.id.search_account_edit_text);
        searchAccountButton = findViewById(R.id.search_account_button);
        accountsRecyclerView = findViewById(R.id.accounts_recycler_view);
        loadingIcon = findViewById(R.id.loading_spinner);

        languageSchoolAPI = LanguageSchoolAPIHelper.getApiObject();

        searchAccountButton.setOnClickListener(view -> {
            q = searchAccountEditText.getText().toString();
            requestPage(1);
        });

        addOnScrollListenerToRecyclerView();
    }

    private void requestPage(int index) {
        isLoading = true;

        if (index == 1) {
            searchAccountButton.setLoading(true);
        } else {
            loadingIcon.setVisibility(View.VISIBLE);
        }

        Call<Page<User>> accountsCall = languageSchoolAPI.getAccounts(q, index);

        handler.postDelayed(() -> accountsCall.enqueue(new Callback<Page<User>>() {
            @Override
            public void onResponse(@NonNull Call<Page<User>> call, @NonNull Response<Page<User>> response) {
                Page<User> returnedPage = response.body();

                if ((response.isSuccessful()) && (returnedPage != null)) {
                    if (index == 1) {
                        page = returnedPage;
                        adapter = new AccountAdapter(SearchAccountActivity.this, page.getResults());
                        accountsRecyclerView.setAdapter(adapter);
                    } else {
                        int pastNumberOfItems = page.getResults().size();
                        List<User> accounts = returnedPage.getResults();

                        page.setNext(returnedPage.getNext());
                        page.addResults(accounts);
                        adapter.notifyItemRangeInserted(pastNumberOfItems, page.getResults().size());
                    }

                    pageIndex = index;
                } else {
                    Toast.makeText(SearchAccountActivity.this, R.string.warning_request_error, Toast.LENGTH_SHORT).show();
                }

                searchAccountButton.setLoading(false);
                loadingIcon.setVisibility(View.GONE);
                isLoading = false;
            }

            @Override
            public void onFailure(@NonNull Call<Page<User>> call, @NonNull Throwable t) {
                Toast.makeText(SearchAccountActivity.this, R.string.warning_connection_error, Toast.LENGTH_SHORT).show();
                searchAccountButton.setLoading(false);
                loadingIcon.setVisibility(View.GONE);
                isLoading = false;
            }
        }), 1000);
    }

    private void addOnScrollListenerToRecyclerView() {
        accountsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (layoutManager != null) {
                    int position = layoutManager.findLastVisibleItemPosition();

                    if ((page.getNext() != null) && (position == page.getResults().size() - 1) && (!isLoading)) {
                        requestPage(pageIndex + 1);
                    }
                }
            }
        });
    }
}
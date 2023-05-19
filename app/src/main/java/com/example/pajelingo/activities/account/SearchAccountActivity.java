package com.example.pajelingo.activities.account;

import android.os.Bundle;
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
import com.example.pajelingo.interfaces.HttpResponseInterface;
import com.example.pajelingo.models.Page;
import com.example.pajelingo.models.User;
import com.example.pajelingo.retrofit_calls.SearchAccountsCall;
import com.example.pajelingo.ui.LoadingButton;

import java.util.List;

import retrofit2.Response;

public class SearchAccountActivity extends AppCompatActivity {
    private EditText searchAccountEditText;
    private LoadingButton searchAccountButton;
    private RecyclerView accountsRecyclerView;
    private ProgressBar loadingIcon;

    private AccountAdapter adapter;

    private String q;
    private int pageIndex = 0;
    private Page<User> page = null;

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

        SearchAccountsCall accountCall = new SearchAccountsCall();

        accountCall.execute(q, index, new HttpResponseInterface<Page<User>>() {
            @Override
            public void onSuccess(Page<User> returnedPage) {
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
                stopLoading();
            }

            @Override
            public void onError(Response<Page<User>> response) {
                Toast.makeText(SearchAccountActivity.this, R.string.warning_request_error, Toast.LENGTH_SHORT).show();
                stopLoading();
            }

            @Override
            public void onFailure() {
                Toast.makeText(SearchAccountActivity.this, R.string.warning_connection_error, Toast.LENGTH_SHORT).show();
                stopLoading();
            }
        });
    }

    private void stopLoading() {
        searchAccountButton.setLoading(false);
        loadingIcon.setVisibility(View.GONE);
        isLoading = false;
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
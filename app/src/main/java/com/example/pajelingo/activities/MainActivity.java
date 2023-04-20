package com.example.pajelingo.activities;

import static com.example.pajelingo.utils.Tools.deleteUserCredentials;
import static com.example.pajelingo.utils.Tools.isUserAuthenticated;
import static com.example.pajelingo.utils.Tools.launchSynchroResources;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pajelingo.R;
import com.example.pajelingo.activities.account.LoginActivity;
import com.example.pajelingo.activities.search_tool.SearchActivity;
import com.example.pajelingo.adapters.GameAdapter;
import com.example.pajelingo.daos.GameDao;
import com.example.pajelingo.database.settings.AppDatabase;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private TextView greetingTextView;
    private ConstraintLayout noDataWarning;
    private RecyclerView gamesRecyclerView;
    private FloatingActionButton searchButton;
    private SharedPreferences sp;
    private GameDao gameDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        greetingTextView = findViewById(R.id.greeting_text_view);
        noDataWarning = findViewById(R.id.no_data_constraint_layout);
        gamesRecyclerView = findViewById(R.id.games_recycler_view);
        searchButton = findViewById(R.id.search_button);

        sp = getSharedPreferences(getString(R.string.sp_file_name),MODE_PRIVATE);

        gameDao = AppDatabase.getInstance(this).getGameDao();

        searchButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SearchActivity.class)));
    }

    private void loadGames(){
        gameDao.getEnabledGames(result -> {
            if (result.isEmpty()){
                noDataWarning.setVisibility(View.VISIBLE);
            }else{
                noDataWarning.setVisibility(View.GONE);
            }
            gamesRecyclerView.setAdapter(new GameAdapter(MainActivity.this, result));
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_appbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedId = item.getItemId();
        // Launch synchronization
        if (selectedId == R.id.action_synchro) {
            askConfirmationSynchroResources();
        }else if (selectedId == R.id.action_menu){
            startActivity(new Intent(this, MenuActivity.class));
        }else if (selectedId == R.id.action_login_logout){
            swapConnexionMode();
        }

        return super.onOptionsItemSelected(item);
    }

    private void askConfirmationSynchroResources() {
        // Ask user's confirmation before launching a synchro
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_download_resources_title)
                .setMessage(R.string.dialog_download_resources_message)
                .setPositiveButton(R.string.dialog_download_resources_confirm, (confirmationDialog, id) -> {
                    // When the user confirms, launches the synchro steps by using a chain of responsibility design pattern
                    AlertDialog downloadDialog = new AlertDialog.Builder(MainActivity.this)
                            .setTitle(R.string.progress_download_title).setMessage(R.string.progress_download_initial_msg)
                            .setCancelable(false).create();
                    downloadDialog.show();
                    downloadDialog.setOnDismissListener(dialog2 -> loadGames());
                    launchSynchroResources(MainActivity.this, downloadDialog);
                    confirmationDialog.dismiss();
                })
                .setNegativeButton(R.string.dialog_download_resources_decline, (dialog, which) -> dialog.dismiss());
        AlertDialog confirmationDialog = builder.create();
        confirmationDialog.show();
    }

    private void swapConnexionMode() {
        if (!isUserAuthenticated(this)){
            startActivity(new Intent(this, LoginActivity.class));
        }else{
            deleteUserCredentials(this);
            // Update menu layout
            onResume();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the app is in the online mode, show the option to pass to offline mode
        MenuItem onlineItem = menu.findItem(R.id.action_login_logout);

        if (!isUserAuthenticated(this)){
            onlineItem.setIcon(R.drawable.ic_login);
            onlineItem.setTitle(R.string.tab_icon_login_title);
        }else{
            onlineItem.setIcon(R.drawable.ic_logout);
            onlineItem.setTitle(R.string.tab_icon_logout_title);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadGames();
        // Greet user if it is logged in
        if (isUserAuthenticated(this)){
            greetingTextView.setVisibility(View.VISIBLE);
            String username = sp.getString(getString(R.string.username_sp), null);
            greetingTextView.setText(getString(R.string.greeting_text)+username);
        }else{
            greetingTextView.setVisibility(View.GONE);
            greetingTextView.setText(null);
        }
        // Verify if it is necessary to change the layout of the menu(the online/offline mode icon)
        invalidateOptionsMenu();
    }
}